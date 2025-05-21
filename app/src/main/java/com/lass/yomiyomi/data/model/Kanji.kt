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
    val level: String                             // 수준 (레벨)
)