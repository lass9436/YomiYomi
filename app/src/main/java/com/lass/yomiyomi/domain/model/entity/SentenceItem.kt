package com.lass.yomiyomi.domain.model.entity

import com.lass.yomiyomi.domain.model.data.InfoRowData

data class SentenceItem(
    val id: Int,
    val japanese: String,           // "私[わたし]は学生[がくせい]です"
    val korean: String,             // "나는 학생입니다"
    val paragraphId: String? = null,
    val orderInParagraph: Int = 0,
    val category: String,
    val difficulty: String,
    val learningProgress: Float = 0f,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long? = null,
    val createdAt: Long
) : Item {
    
    override fun getMainText(): String = japanese
    
    override fun toInfoRows(): List<InfoRowData> = listOf(
        InfoRowData("일본어 :", japanese, isJapanese = true),
        InfoRowData("한국어 :", korean, isJapanese = false),
        InfoRowData("카테고리 :", category, isJapanese = false),
        InfoRowData("난이도 :", difficulty, isJapanese = false),
        InfoRowData("진도 :", "${(learningProgress * 100).toInt()}%", isJapanese = false)
    )
} 
