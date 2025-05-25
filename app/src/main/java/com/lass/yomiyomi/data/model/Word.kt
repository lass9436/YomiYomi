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
    val level: String
)