package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_kanji")
data class MyKanji(
    @PrimaryKey
    val id: Int,
    val kanji: String,
    val onyomi: String,
    val kunyomi: String,
    val meaning: String,
    val level: String,
    val learningWeight: Float,
    val timestamp: Long
) 