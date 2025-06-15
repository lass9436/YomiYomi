package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "paragraph_list_mapping",
    primaryKeys = ["listId", "paragraphId"],
    foreignKeys = [
        ForeignKey(
            entity = ParagraphList::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE // 리스트 삭제 시 매핑만 삭제
        ),
        ForeignKey(
            entity = MyParagraph::class,
            parentColumns = ["paragraphId"],
            childColumns = ["paragraphId"],
            onDelete = ForeignKey.CASCADE // 문단 삭제 시 매핑만 삭제
        )
    ]
)
data class ParagraphListMapping(
    val listId: Int,
    val paragraphId: Int,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

