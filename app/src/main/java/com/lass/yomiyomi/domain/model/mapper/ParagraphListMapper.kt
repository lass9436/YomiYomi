package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.ParagraphList
import com.lass.yomiyomi.data.model.ParagraphListMapping
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListMappingItem

fun ParagraphList.toParagraphListItem(): ParagraphListItem = ParagraphListItem(
    listId = listId,
    name = name,
    description = description,
    createdAt = createdAt
)

fun ParagraphListItem.toParagraphList(): ParagraphList = ParagraphList(
    listId = listId,
    name = name,
    description = description,
    createdAt = createdAt
)

fun ParagraphListMapping.toParagraphListMappingItem(): ParagraphListMappingItem = ParagraphListMappingItem(
    listId = listId,
    paragraphId = paragraphId,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun ParagraphListMappingItem.toParagraphListMapping(): ParagraphListMapping = ParagraphListMapping(
    listId = listId,
    paragraphId = paragraphId,
    sortOrder = sortOrder,
    createdAt = createdAt
)