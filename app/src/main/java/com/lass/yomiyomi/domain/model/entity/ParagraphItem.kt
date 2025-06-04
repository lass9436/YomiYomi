package com.lass.yomiyomi.domain.model.entity

import com.lass.yomiyomi.domain.model.data.InfoRowData
import com.lass.yomiyomi.domain.model.constant.Level

data class ParagraphItem(
    val paragraphId: String,
    val title: String,
    val description: String,
    val category: String,
    val level: Level,
    val totalSentences: Int,
    val actualSentenceCount: Int = 0,  // 실제 등록된 문장 수
    val createdAt: Long
) : Item {
    
    override fun getMainText(): String = title
    
    override fun toInfoRows(): List<InfoRowData> = listOf(
        InfoRowData("제목 :", title, isJapanese = false),
        InfoRowData("설명 :", description, isJapanese = false),
        InfoRowData("카테고리 :", category, isJapanese = false),
        InfoRowData("레벨 :", level.value ?: "ALL", isJapanese = false),
        InfoRowData("문장 수 :", "$actualSentenceCount/$totalSentences", isJapanese = false)
    )
} 
