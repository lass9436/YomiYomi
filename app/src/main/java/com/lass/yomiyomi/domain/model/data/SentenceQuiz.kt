package com.lass.yomiyomi.domain.model.data

data class SentenceQuiz(
    val question: String,          // 보여줄 문제 (한국어 또는 일본어)
    val correctAnswer: String,     // 정답 일본어 텍스트
    val cleanAnswer: String,       // 정답 일본어 (후리가나 제거, 음성 비교용)
    val sentenceId: Int           // 문장 ID (학습 진도 업데이트용)
) 