package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji")
data class Kanji(
    @PrimaryKey
    val id: Int,
    val kanji: String,                            // 한자
    val onyomi: String,                           // 음독
    val kunyomi: String,                          // 훈독
    val meaning: String,                          // 뜻
    val level: String,                            // 수준 (레벨)
    val learningWeight: Float,                    // 가중치
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