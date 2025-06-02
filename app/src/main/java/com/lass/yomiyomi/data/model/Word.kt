package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class Word (
    @PrimaryKey
    val id: Int,
    val word: String,
    val reading: String,
    val type: String,
    val meaning: String,
    val level: String,
    val learningWeight: Float,
    val timestamp: Long
) : Item {
    
    override fun getMainText(): String = word
    
    override fun toInfoRows(): List<InfoRowData> = listOf(
        InfoRowData("읽기 :", reading, isJapanese = true),
        InfoRowData("품사 :", type, isJapanese = false),
        InfoRowData("의미 :", meaning, isJapanese = false),
        InfoRowData("레벨 :", level, isJapanese = false)
    )
}