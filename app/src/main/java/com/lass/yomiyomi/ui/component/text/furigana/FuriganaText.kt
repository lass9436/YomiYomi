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
    
    // 동적 너비 계산 및 줄바꿈
    BoxWithConstraints(modifier = modifier) {
        val availableWidth = maxWidth
        val availableWidthPx = with(density) { availableWidth.toPx() }
        
        // 각 세그먼트별 너비 계산 및 줄 분할
        val lines = remember(segments, availableWidthPx, fontSize) {
            calculateLines(segments, availableWidthPx, textMeasurer, fontSize, furiganaSize, displayMode)
        }
        
        // 각 줄을 개별 Row로 렌더링
        Column {
            lines.forEach { lineSegments ->
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    lineSegments.forEach { segment ->
                        when {
                            // 한자 + 요미가나 세그먼트
                            segment.furigana != null -> {
                                when (displayMode) {
                                    DisplayMode.FULL, DisplayMode.JAPANESE_ONLY -> {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 1.dp),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy((-3).dp)
                                            ) {
                                                // 퀴즈 모드에서 후리가나 표시 결정
                                                val displayFurigana = if (quiz != null) {
                                                    // 퀴즈 모드: 빈칸인지 확인
                                                    val blankForThisFurigana = quiz.blanks.find { it.correctAnswer == segment.furigana }
                                                    if (blankForThisFurigana != null) {
                                                        // 빈칸이면 채워진 답 또는 ___
                                                        quiz.filledBlanks[blankForThisFurigana.index] ?: "___"
                                                    } else {
                                                        // 빈칸이 아니면 원래 후리가나
                                                        segment.furigana
                                                    }
                                                } else {
                                                    // 일반 모드: 원래 후리가나
                                                    segment.furigana
                                                }
                                                
                                                Text(
                                                    text = displayFurigana,
                                                    fontSize = furiganaSize,
                                                    color = if (quiz != null && displayFurigana == "___") {
                                                        MaterialTheme.colorScheme.error
                                                    } else if (quiz != null && displayFurigana != segment.furigana) {
                                                        MaterialTheme.colorScheme.primary  
                                                    } else {
                                                        MaterialTheme.colorScheme.outline
                                                    },
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
                                    DisplayMode.JAPANESE_NO_FURIGANA -> {
                                        Text(
                                            text = segment.text,
                                            fontSize = fontSize
                                        )
                                    }
                                    DisplayMode.KOREAN_ONLY -> {
                                        // 한국어만 모드에서는 일본어 텍스트 숨김
                                    }
                                }
                            }
                            // 일반 텍스트 (히라가나/카타카나)
                            else -> {
                                when (displayMode) {
                                    DisplayMode.KOREAN_ONLY -> {
                                        // 한국어만 모드에서는 히라가나/카타카나 숨김
                                    }
                                    else -> {
                                        Text(
                                            text = segment.text,
                                            fontSize = fontSize
                                        )
                                    }
                                }
                            }
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
    displayMode: DisplayMode
): List<List<com.lass.yomiyomi.domain.model.data.TextSegment>> {
    val lines = mutableListOf<List<com.lass.yomiyomi.domain.model.data.TextSegment>>()
    var currentLine = mutableListOf<com.lass.yomiyomi.domain.model.data.TextSegment>()
    var currentLineWidth = 0f
    
    segments.forEach { segment ->
        // 각 세그먼트의 렌더링 너비 계산
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
                        maxOf(kanjiWidth, furiganaWidth) + 8f // padding 고려
                    }
                    DisplayMode.JAPANESE_NO_FURIGANA -> {
                        textMeasurer.measure(
                            text = segment.text,
                            style = TextStyle(fontSize = fontSize)
                        ).size.width.toFloat()
                    }
                    DisplayMode.KOREAN_ONLY -> {
                        // 한국어만 모드에서는 일본어 텍스트 숨김 - 너비 0
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
                        ).size.width.toFloat()
                    }
                }
            }
        }
        
        // 줄바꿈 판단
        if (currentLineWidth + segmentWidth <= availableWidthPx) {
            currentLine.add(segment)
            currentLineWidth += segmentWidth
        } else {
            // 현재 줄이 비어있지 않으면 저장하고 새 줄 시작
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.toList())
                currentLine.clear()
                currentLineWidth = 0f
            }
            currentLine.add(segment)
            currentLineWidth = segmentWidth
        }
    }
    
    // 마지막 줄 추가
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toList())
    }
    
    return lines
}
