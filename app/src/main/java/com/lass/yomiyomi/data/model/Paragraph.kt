package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paragraph")
data class Paragraph(
    @PrimaryKey val paragraphId: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val totalSentences: Int,
    val createdAt: Long = System.currentTimeMillis()
) 
