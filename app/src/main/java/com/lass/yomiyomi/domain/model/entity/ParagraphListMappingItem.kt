package com.lass.yomiyomi.domain.model.entity

data class ParagraphListMappingItem(
    val listId: Int,
    val paragraphId: Int,
    val sortOrder: Int,
    val createdAt: Long
)