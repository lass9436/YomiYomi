package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.ui.graphics.Color
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.data.TextSegment
import com.lass.yomiyomi.ui.theme.CustomColorScheme
import com.lass.yomiyomi.util.FuriganaParser

object FuriganaColors {
    fun getFuriganaColor(
        isKanaDummy: Boolean,
        isBlank: Boolean,
        isCorrect: Boolean,
        customColors: CustomColorScheme
    ): Color = when {
        isKanaDummy -> Color.Transparent
        isBlank -> customColors.quizBlankBg
        isCorrect -> customColors.quizFilled
        else -> customColors.furigana
    }

    fun getFuriganaColorForSegment(
        segment: TextSegment,
        quiz: ParagraphQuiz?,
        customColors: CustomColorScheme
    ): Color {
        val isKanaDummy = isKanaOrDigitDummy(segment)
        val isBlank = isSegmentBlank(segment, quiz)
        val isCorrect = isSegmentCorrect(segment, quiz)

        return getFuriganaColor(isKanaDummy, isBlank, isCorrect, customColors)
    }

    fun isKanaOrDigitDummy(segment: TextSegment): Boolean {
        return segment.furigana == null && 
            (segment.text.all { FuriganaParser.isKana(it) } || segment.text.all { it.isDigit() })
    }

    fun isSegmentBlank(segment: TextSegment, quiz: ParagraphQuiz?): Boolean {
        return quiz?.blanks?.any { it.correctAnswer == segment.furigana } == true &&
            quiz.filledBlanks[quiz.blanks.first { it.correctAnswer == segment.furigana }.index] == null
    }

    fun isSegmentCorrect(segment: TextSegment, quiz: ParagraphQuiz?): Boolean {
        return quiz?.filledBlanks?.any { it.value == segment.furigana } == true
    }

    fun getDisplayFurigana(segment: TextSegment, quiz: ParagraphQuiz?): String {
        return segment.furigana?.let {
            quiz?.blanks
                ?.find { blank -> blank.correctAnswer == it }
                ?.let { blank -> quiz.filledBlanks[blank.index] ?: it } ?: it
        } ?: " "
    }

    fun getFakeFurigana(segment: TextSegment, displayFurigana: String): String {
        return segment.furigana?.let { displayFurigana } ?: if (isKanaOrDigitDummy(segment)) "あ" else " "
    }
} 