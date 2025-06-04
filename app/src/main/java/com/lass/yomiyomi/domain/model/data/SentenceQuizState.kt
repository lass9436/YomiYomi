package com.lass.yomiyomi.domain.model.data

import com.lass.yomiyomi.domain.model.entity.SentenceItem

data class SentenceQuizState(
    val currentQuestion: SentenceItem? = null,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val score: Int = 0,
    val isAnswered: Boolean = false,
    val isQuizFinished: Boolean = false,
    val showAnswer: Boolean = false
) 