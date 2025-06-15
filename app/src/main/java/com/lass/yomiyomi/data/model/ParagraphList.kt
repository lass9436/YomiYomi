package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paragraph_list")
data class ParagraphList(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
