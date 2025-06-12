package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.ui.theme.LocalCustomColors
import com.lass.yomiyomi.util.FuriganaParser

@Composable
fun FuriganaText(
    text: String,
    fontSize: TextUnit = 16.sp,
    furiganaSize: TextUnit = (fontSize.value * 0.6).sp,
    displayMode: DisplayMode = DisplayMode.FULL,
    quiz: ParagraphQuiz? = null,
    modifier: Modifier = Modifier,
    maxWidth: Dp = Dp.Unspecified
) {
    val segments = remember(text) {
        FuriganaParser.parse(text)
    }
    
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val customColors = LocalCustomColors.current
    
    BoxWithConstraints(modifier = modifier) {
        val availableWidthPx = with(density) {
            (this@BoxWithConstraints.maxWidth - 60.dp).toPx().toInt()
        }
        
        val lines = remember(segments, availableWidthPx, textMeasurer, fontSize, furiganaSize, displayMode, density, quiz) {
            FuriganaTextLayout.calculateLines(
                segments = segments,
                availableWidthPx = availableWidthPx,
                textMeasurer = textMeasurer,
                fontSize = fontSize,
                furiganaSize = furiganaSize,
                displayMode = displayMode,
                density = density,
                quiz = quiz
            )
        }
        
        Column {
            lines.forEach { lineSegments ->
                Row(
                    horizontalArrangement = Arrangement.Start,
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
