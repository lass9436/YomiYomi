package com.lass.yomiyomi.domain.model.data

import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem

data class ParagraphQuizState(
    val currentParagraph: ParagraphItem? = null,
    val currentSentences: List<SentenceItem> = emptyList(),
    val currentSentenceIndex: Int = 0,
    val currentParagraphIndex: Int = 0,
    val totalParagraphs: Int = 0,
    val score: Int = 0,
    val isAnswered: Boolean = false,
    val isQuizFinished: Boolean = false,
    val showAnswer: Boolean = false
) 