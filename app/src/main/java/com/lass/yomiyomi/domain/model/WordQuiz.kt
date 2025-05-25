package com.lass.yomiyomi.domain.model

data class WordQuiz (
    val question: String,
    val answer: String,
    val options: List<String>,
    val correctIndex: Int
)