package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.data.*
import com.lass.yomiyomi.util.FuriganaParser
import com.lass.yomiyomi.ui.theme.LocalCustomColors

@Composable
fun FuriganaText(
    japaneseText: String,
    displayMode: DisplayMode = DisplayMode.FULL,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    furiganaSize: TextUnit = (fontSize.value * 0.6).sp,
    quiz: ParagraphQuiz? = null
) {
    val segments = remember(japaneseText) {
        FuriganaParser.parse(japaneseText)
    }

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val customColors = LocalCustomColors.current

    BoxWithConstraints(modifier = modifier) {
        val availableWidthPx = with(density) { maxWidth.toPx() }

        val lines = remember(segments, availableWidthPx, fontSize) {
            calculateLines(segments, availableWidthPx, textMeasurer, fontSize, furiganaSize, displayMode, density, quiz)
        }

        Column {
            lines.forEach { lineSegments ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    lineSegments.forEach { segment ->
                        FuriganaTextSegment(
                            segment = segment,
                            displayMode = displayMode,
                            fontSize = fontSize,
                            furiganaSize = furiganaSize,
                            quiz = quiz,
                            customColors = customColors
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FuriganaTextSegment(
    segment: TextSegment,
    displayMode: DisplayMode,
    fontSize: TextUnit,
    furiganaSize: TextUnit,
    quiz: ParagraphQuiz?,
    customColors: com.lass.yomiyomi.ui.theme.CustomColorScheme
) {
    val isKanaDummy = segment.furigana == null && (segment.text.all { FuriganaParser.isKana(it) } || segment.text.all { it.isDigit() })
    val isBlank = quiz?.blanks?.any { it.correctAnswer == segment.furigana } == true &&
            quiz.filledBlanks[quiz.blanks.first { it.correctAnswer == segment.furigana }.index] == null
    val isCorrect = quiz?.filledBlanks?.any { it.value == segment.furigana } == true

    val blankBgColor = customColors.quizBlankBg
    val blankFgColor = blankBgColor
    val correctFgColor = customColors.quizFilled

    val furiganaColor = when {
        isKanaDummy -> Color.Transparent
        isBlank -> blankFgColor
        isCorrect -> correctFgColor
        else -> customColors.furigana
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 1.dp)
    ) {
        if (displayMode != DisplayMode.JAPANESE_NO_FURIGANA) {
            FuriganaTextFuriganaBox(
                segment = segment,
                furiganaSize = furiganaSize,
                furiganaColor = furiganaColor,
                isBlank = isBlank,
                blankBgColor = blankBgColor,
                quiz = quiz
            )
        }
        Text(
            text = segment.text,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            lineHeight = fontSize * 0.9f
        )
    }
}

@Composable
private fun FuriganaTextFuriganaBox(
    segment: TextSegment,
    furiganaSize: TextUnit,
    furiganaColor: Color,
    isBlank: Boolean,
    blankBgColor: Color,
    quiz: ParagraphQuiz?
) {
    Box(
        modifier = if (isBlank) Modifier.background(blankBgColor, shape = RoundedCornerShape(4.dp)) else Modifier
    ) {
        val displayFurigana = segment.furigana?.let {
            quiz?.blanks
                ?.find { blank -> blank.correctAnswer == it }
                ?.let { blank -> quiz.filledBlanks[blank.index] ?: it } ?: it
        } ?: " "

        val isKanaOrDigit = segment.text.all { FuriganaParser.isKana(it) } || segment.text.all { it.isDigit() }
        val fakeFurigana = segment.furigana?.let { displayFurigana } ?: if (isKanaOrDigit) "あ" else " "

        Text(
            text = fakeFurigana,
            fontSize = furiganaSize,
            color = furiganaColor.takeIf { segment.furigana != null } ?: Color.Transparent,
            textAlign = TextAlign.Center,
            lineHeight = furiganaSize * 0.8f
        )
    }
}

private fun calculateLines(
    segments: List<TextSegment>,
    availableWidthPx: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    fontSize: TextUnit,
    furiganaSize: TextUnit,
    displayMode: DisplayMode,
    density: androidx.compose.ui.unit.Density,
    quiz: ParagraphQuiz? = null
): List<List<TextSegment>> {
    val lines = mutableListOf<List<TextSegment>>()
    var currentLine = mutableListOf<TextSegment>()
    var currentLineWidth = 0f
    val segmentPadding = with(density) { 4.dp.toPx() }

    for (segment in segments) {
        val subSegments = splitSegmentIfNeeded(segment, availableWidthPx, currentLineWidth, textMeasurer, fontSize, segmentPadding, density)

        for (subSegment in subSegments) {
            val width = calculateSegmentWidth(subSegment, textMeasurer, fontSize, furiganaSize, displayMode, quiz, segmentPadding)

            if (currentLineWidth + width > availableWidthPx && currentLine.isNotEmpty()) {
                lines.add(currentLine.toList())
                currentLine.clear()
                currentLineWidth = 0f
            }

            currentLine.add(subSegment)
            currentLineWidth += width
        }
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine)
    }

    return lines
}

private fun splitSegmentIfNeeded(
    segment: TextSegment,
    availableWidthPx: Float,
    currentLineWidth: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    fontSize: TextUnit,
    segmentPadding: Float,
    density: androidx.compose.ui.unit.Density
): List<TextSegment> {
    if (segment.furigana == null && segment.text.length > 1) {
        val result = mutableListOf<TextSegment>()
        var idx = 0
        var lineWidth = currentLineWidth

        while (idx < segment.text.length) {
            var chunk = ""
            var chunkWidth = 0f

            while (idx < segment.text.length) {
                val nextChar = segment.text[idx].toString()
                val nextWidth = textMeasurer.measure(nextChar, TextStyle(fontSize = fontSize)).size.width.toFloat() + segmentPadding

                if (lineWidth + chunkWidth + nextWidth > availableWidthPx && chunk.isNotEmpty()) break

                chunk += nextChar
                chunkWidth += nextWidth
                idx++
            }

            result.add(TextSegment(chunk, null))
            lineWidth = if (lineWidth + chunkWidth > availableWidthPx) 0f else lineWidth + chunkWidth
        }

        return result
    }
    return listOf(segment)
}

private fun calculateSegmentWidth(
    segment: TextSegment,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    fontSize: TextUnit,
    furiganaSize: TextUnit,
    displayMode: DisplayMode,
    quiz: ParagraphQuiz?,
    padding: Float
): Float {
    return when {
        segment.furigana != null -> {
            when (displayMode) {
                DisplayMode.FULL, DisplayMode.JAPANESE_ONLY -> {
                    val displayFurigana = quiz?.blanks
                        ?.find { it.correctAnswer == segment.furigana }
                        ?.let { quiz.filledBlanks[it.index] ?: it.correctAnswer }
                        ?: segment.furigana

                    val kanjiWidth = textMeasurer.measure(segment.text, TextStyle(fontSize = fontSize)).size.width.toFloat()
                    val furiganaWidth = textMeasurer.measure(displayFurigana ?: "", TextStyle(fontSize = furiganaSize)).size.width.toFloat()

                    maxOf(kanjiWidth, furiganaWidth) + padding
                }

                DisplayMode.JAPANESE_NO_FURIGANA -> {
                    textMeasurer.measure(segment.text, TextStyle(fontSize = fontSize)).size.width.toFloat() + padding
                }

                DisplayMode.KOREAN_ONLY -> 0f
            }
        }

        else -> {
            if (displayMode == DisplayMode.KOREAN_ONLY) 0f
            else textMeasurer.measure(segment.text, TextStyle(fontSize = fontSize)).size.width.toFloat() + padding
        }
    }
}

fun colorLerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = (start.red + (stop.red - start.red) * fraction),
        green = (start.green + (stop.green - start.green) * fraction),
        blue = (start.blue + (stop.blue - start.blue) * fraction),
        alpha = (start.alpha + (stop.alpha - start.alpha) * fraction)
    )
}
