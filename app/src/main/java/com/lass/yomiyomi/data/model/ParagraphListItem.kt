package com.lass.yomiyomi.data.model

import androidx.room.Entity

@Entity(
    tableName = "paragraph_list_item",
    primaryKeys = ["listId", "paragraphId"]
)
data class ParagraphListItem(
    val listId: Int,
    val paragraphId: Int,
    val sortOrder: Int = 0,  // 필요 없으면 제거 가능
    val createdAt: Long = System.currentTimeMillis()
)

