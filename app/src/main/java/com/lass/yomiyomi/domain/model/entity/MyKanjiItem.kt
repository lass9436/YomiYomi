package com.lass.yomiyomi.domain.model.entity

import com.lass.yomiyomi.domain.model.data.InfoRowData

data class MyKanjiItem(
    val id: Int,
    val kanji: String,
    val onyomi: String,
    val kunyomi: String,
    val meaning: String,
    val level: String,
    val learningWeight: Float,
    val timestamp: Long
) : Item {
    
    override fun getMainText(): String = kanji
    
    override fun toInfoRows(): List<InfoRowData> = listOf(
        InfoRowData("음독 :", onyomi, isJapanese = true),
        InfoRowData("훈독 :", kunyomi, isJapanese = true),
        InfoRowData("의미 :", meaning, isJapanese = false),
        InfoRowData("레벨 :", level, isJapanese = false)
    )
} 
