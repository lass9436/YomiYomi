package com.lass.yomiyomi.domain.model

// 텍스트 세그먼트 데이터 클래스
data class TextSegment(
    val text: String,
    val furigana: String? = null  // null이면 일반 텍스트
) 