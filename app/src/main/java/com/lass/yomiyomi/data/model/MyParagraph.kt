package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paragraph")
data class MyParagraph(
    @PrimaryKey val paragraphId: Int,
    val title: String,
    val description: String,
    val category: String,
    val level: String,
    val totalSentences: Int,
    val createdAt: Long = System.currentTimeMillis()
)
