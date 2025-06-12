package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.data.TextSegment
import com.lass.yomiyomi.ui.theme.CustomColorScheme

@Composable
internal fun FuriganaTextSegment(
    segment: TextSegment,
    displayMode: DisplayMode,
    fontSize: TextUnit,
    furiganaSize: TextUnit,
    quiz: ParagraphQuiz?,
    customColors: CustomColorScheme
) {
    val furiganaColor = remember(segment, quiz, customColors) {
        FuriganaColors.getFuriganaColorForSegment(segment, quiz, customColors)
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
                isBlank = FuriganaColors.isSegmentBlank(segment, quiz),
                blankBgColor = customColors.quizBlankBg,
                quiz = quiz
            )
        }
        FuriganaTextMain(
            text = segment.text,
            fontSize = fontSize
        )
    }
}

@Composable
private fun FuriganaTextMain(
    text: String,
    fontSize: TextUnit
) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        lineHeight = fontSize * 0.9f
    )
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
        val displayFurigana = remember(segment, quiz) {
            FuriganaColors.getDisplayFurigana(segment, quiz)
        }

        val fakeFurigana = remember(segment, displayFurigana) {
            FuriganaColors.getFakeFurigana(segment, displayFurigana)
        }

        Text(
            text = fakeFurigana,
            fontSize = furiganaSize,
            color = furiganaColor.takeIf { segment.furigana != null } ?: Color.Transparent,
            textAlign = TextAlign.Center,
            lineHeight = furiganaSize * 0.8f
        )
    }
} 