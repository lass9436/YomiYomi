package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.data.TextSegment
import kotlin.math.max

object FuriganaTextLayout {
    fun calculateLines(
        segments: List<TextSegment>,
        availableWidthPx: Int,
        textMeasurer: TextMeasurer,
        fontSize: TextUnit,
        furiganaSize: TextUnit,
        displayMode: DisplayMode,
        density: Density,
        quiz: ParagraphQuiz?
    ): List<List<TextSegment>> {
        val lines = mutableListOf<List<TextSegment>>()
        var currentLine = mutableListOf<TextSegment>()
        var currentLineWidth = 0f

        segments.forEach { segment ->
            val segmentWidth = calculateSegmentWidth(
                segment = segment,
                textMeasurer = textMeasurer,
                fontSize = fontSize,
                furiganaSize = furiganaSize,
                displayMode = displayMode
            )

            if (currentLineWidth + segmentWidth > availableWidthPx) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toList())
                    currentLine = mutableListOf()
                    currentLineWidth = 0f
                }
                currentLine.add(segment)
                currentLineWidth = segmentWidth
            } else {
                currentLine.add(segment)
                currentLineWidth += segmentWidth
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    private fun calculateSegmentWidth(
        segment: TextSegment,
        textMeasurer: TextMeasurer,
        fontSize: TextUnit,
        furiganaSize: TextUnit,
        displayMode: DisplayMode
    ): Float {
        // 한자의 너비 계산
        val mainTextWidth = textMeasurer.measure(
            text = segment.text,
            style = androidx.compose.ui.text.TextStyle(fontSize = fontSize)
        ).size.width.toFloat()

        // 후리가나가 있는 경우 후리가나의 너비도 계산
        val furiganaWidth = segment.furigana?.let {
            textMeasurer.measure(
                text = it,
                style = androidx.compose.ui.text.TextStyle(fontSize = furiganaSize)
            ).size.width.toFloat()
        } ?: 0f

        // 한자와 후리가나 중 더 큰 너비를 사용하고 패딩 추가
        return maxOf(mainTextWidth, furiganaWidth) + 2f
    }

    fun createMeasurePolicy(lines: List<List<TextSegment>>): MeasurePolicy {
        return MeasurePolicy { measurables, constraints ->
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            var maxLineWidth = 0
            var totalHeight = 0
            var placeableIndex = 0

            lines.forEach { lineSegments ->
                var lineWidth = 0
                val lineHeight = placeables.subList(
                    placeableIndex,
                    placeableIndex + lineSegments.size
                ).maxOfOrNull { it.height } ?: 0

                lineSegments.forEach { _ ->
                    lineWidth += placeables[placeableIndex].width
                    placeableIndex++
                }

                maxLineWidth = max(maxLineWidth, lineWidth)
                totalHeight += lineHeight
            }

            layout(maxLineWidth, totalHeight) {
                var currentY = 0
                placeableIndex = 0

                lines.forEach { lineSegments ->
                    var currentX = 0
                    val lineHeight = placeables.subList(
                        placeableIndex,
                        placeableIndex + lineSegments.size
                    ).maxOfOrNull { it.height } ?: 0

                    lineSegments.forEach { _ ->
                        val placeable = placeables[placeableIndex]
                        placeable.place(currentX, currentY)
                        currentX += placeable.width
                        placeableIndex++
                    }

                    currentY += lineHeight
                }
            }
        }
    }
} 
