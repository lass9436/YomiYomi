package com.lass.yomiyomi.domain.model

data class KanjiQuiz(
    val question: String,
    val answer: String,
    val options: List<String>,
    val correctIndex: Int
)