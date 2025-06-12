package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.util.FuriganaParser
import com.lass.yomiyomi.ui.theme.LocalCustomColors
import androidx.compose.ui.graphics.Color

@Composable
fun FuriganaText(
    japaneseText: String,
    displayMode: DisplayMode = DisplayMode.FULL,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    furiganaSize: TextUnit = (fontSize.value * 0.6).sp,
    quiz: com.lass.yomiyomi.domain.model.data.ParagraphQuiz? = null
) {
    val segments = remember(japaneseText) { 
        FuriganaParser.parse(japaneseText) 
    }
    
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val customColors = LocalCustomColors.current
    
    // 동적 너비 계산 및 줄바꿈
    BoxWithConstraints(modifier = modifier) {
        val availableWidth = maxWidth
        val availableWidthPx = with(density) { availableWidth.toPx() }
        
        // 각 세그먼트별 너비 계산 및 줄 분할
        val lines = remember(segments, availableWidthPx, fontSize) {
            calculateLines(segments, availableWidthPx, textMeasurer, fontSize, furiganaSize, displayMode, density)
        }
        
        // 각 줄을 개별 Row로 렌더링
        Column {
            lines.forEach { lineSegments ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    lineSegments.forEach { segment ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        ) {
                            val displayFurigana = if (segment.furigana != null) {
                                if (quiz != null) {
                                    val blankForThisFurigana = quiz.blanks.find { it.correctAnswer == segment.furigana }
                                    if (blankForThisFurigana != null) {
                                        quiz.filledBlanks[blankForThisFurigana.index] ?: "___"
                                    } else {
                                        segment.furigana
                                    }
                                } else {
                                    segment.furigana
                                }
                            } else {
                                " "
                            }
                            val fakeFurigana = if (segment.furigana != null) displayFurigana else "あ"
                            val furiganaColor = if (segment.furigana != null) {
                                if (quiz != null && displayFurigana == "___") {
                                    customColors.quizBlank
                                } else if (quiz != null && displayFurigana != segment.furigana) {
                                    customColors.quizFilled
                                } else {
                                    customColors.furigana
                                }
                            } else {
                                Color.Transparent
                            }
                            Text(
                                text = fakeFurigana,
                                fontSize = furiganaSize,
                                color = furiganaColor,
                                textAlign = TextAlign.Center,
                                lineHeight = furiganaSize * 0.8f
                            )
                            Text(
                                text = segment.text,
                                fontSize = fontSize,
                                textAlign = TextAlign.Center,
                                lineHeight = fontSize * 0.9f
                            )
                        }
                    }
                }
            }
        }
    }
}

// 세그먼트들을 줄별로 분할하는 함수
private fun calculateLines(
    segments: List<com.lass.yomiyomi.domain.model.data.TextSegment>,
    availableWidthPx: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    fontSize: TextUnit,
    furiganaSize: TextUnit,
    displayMode: DisplayMode,
    density: androidx.compose.ui.unit.Density
): List<List<com.lass.yomiyomi.domain.model.data.TextSegment>> {
    val lines = mutableListOf<List<com.lass.yomiyomi.domain.model.data.TextSegment>>()
    var currentLine = mutableListOf<com.lass.yomiyomi.domain.model.data.TextSegment>()
    var currentLineWidth = 0f
    val segmentHorizontalPaddingPx = with(density) { 4.dp.toPx() }
    
    segments.forEach { segment ->
        if (segment.furigana == null && segment.text.length > 1) {
            // 가나 블록: 한 글자씩 추가하며 남은 너비 초과 시 끊기
            var kanaIdx = 0
            while (kanaIdx < segment.text.length) {
                var chunk = ""
                var chunkWidth = 0f
                while (kanaIdx < segment.text.length) {
                    val nextChar = segment.text[kanaIdx].toString()
                    val nextWidth = textMeasurer.measure(
                        text = nextChar,
                        style = TextStyle(fontSize = fontSize)
                    ).size.width.toFloat() + segmentHorizontalPaddingPx
                    if (currentLineWidth + chunkWidth + nextWidth > availableWidthPx && chunk.isNotEmpty()) {
                        break // 이 chunk는 다음 줄로 넘김
                    }
                    chunk += nextChar
                    chunkWidth += nextWidth
                    kanaIdx++
                }
                if (currentLineWidth + chunkWidth > availableWidthPx && currentLine.isNotEmpty()) {
                    lines.add(currentLine.toList())
                    currentLine.clear()
                    currentLineWidth = 0f
                }
                currentLine.add(com.lass.yomiyomi.domain.model.data.TextSegment(chunk, null))
                currentLineWidth += chunkWidth
                if (currentLineWidth >= availableWidthPx) {
                    lines.add(currentLine.toList())
                    currentLine.clear()
                    currentLineWidth = 0f
                }
            }
        } else {
            // 기존 한자+요미가나 블록 처리
            val segmentWidth = when {
                segment.furigana != null -> {
                    when (displayMode) {
                        DisplayMode.FULL, DisplayMode.JAPANESE_ONLY -> {
                            val kanjiWidth = textMeasurer.measure(
                                text = segment.text,
                                style = TextStyle(fontSize = fontSize)
                            ).size.width.toFloat()
                            val furiganaWidth = textMeasurer.measure(
                                text = segment.furigana,
                                style = TextStyle(fontSize = furiganaSize)
                            ).size.width.toFloat()
                            maxOf(kanjiWidth, furiganaWidth) + segmentHorizontalPaddingPx
                        }
                        DisplayMode.JAPANESE_NO_FURIGANA -> {
                            textMeasurer.measure(
                                text = segment.text,
                                style = TextStyle(fontSize = fontSize)
                            ).size.width.toFloat() + segmentHorizontalPaddingPx
                        }
                        DisplayMode.KOREAN_ONLY -> {
                            0f
                        }
                    }
                }
                else -> {
                    when (displayMode) {
                        DisplayMode.KOREAN_ONLY -> 0f
                        else -> {
                            textMeasurer.measure(
                                text = segment.text,
                                style = TextStyle(fontSize = fontSize)
                            ).size.width.toFloat() + segmentHorizontalPaddingPx
                        }
                    }
                }
            }
            if (currentLineWidth + segmentWidth <= availableWidthPx) {
                currentLine.add(segment)
                currentLineWidth += segmentWidth
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toList())
                    currentLine.clear()
                    currentLineWidth = 0f
                }
                currentLine.add(segment)
                currentLineWidth = segmentWidth
            }
        }
    }
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toList())
    }
    return lines
}
