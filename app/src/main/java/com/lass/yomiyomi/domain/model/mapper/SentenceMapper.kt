package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.SentenceEntity
import com.lass.yomiyomi.data.model.ParagraphEntity
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem

// Entity -> Domain Model (조회용)
// SentenceEntity -> SentenceItem
fun SentenceEntity.toSentenceItem(): SentenceItem = SentenceItem(
    id = id,
    japanese = japanese,
    korean = korean,
    paragraphId = paragraphId,
    orderInParagraph = orderInParagraph,
    category = category,
    difficulty = difficulty,
    learningProgress = learningProgress,
    reviewCount = reviewCount,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt
)

// ParagraphEntity -> ParagraphItem
fun ParagraphEntity.toParagraphItem(actualSentenceCount: Int = 0): ParagraphItem = ParagraphItem(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    totalSentences = totalSentences,
    actualSentenceCount = actualSentenceCount,
    createdAt = createdAt
)

// Domain Model -> Entity (CRUD용)
// SentenceItem -> SentenceEntity
fun SentenceItem.toSentenceEntity(): SentenceEntity = SentenceEntity(
    id = id,
    japanese = japanese,
    korean = korean,
    paragraphId = paragraphId,
    orderInParagraph = orderInParagraph,
    category = category,
    difficulty = difficulty,
    learningProgress = learningProgress,
    reviewCount = reviewCount,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt
)

// ParagraphItem -> ParagraphEntity
fun ParagraphItem.toParagraphEntity(): ParagraphEntity = ParagraphEntity(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    totalSentences = totalSentences,
    createdAt = createdAt
)

// List 변환 함수들 (Entity -> Domain)
fun List<SentenceEntity>.toSentenceItems(): List<SentenceItem> = map { it.toSentenceItem() }
fun List<ParagraphEntity>.toParagraphItems(sentenceCounts: Map<String, Int> = emptyMap()): List<ParagraphItem> =
    map { it.toParagraphItem(sentenceCounts[it.paragraphId] ?: 0) }

// List 변환 함수들 (Domain -> Entity)
fun List<SentenceItem>.toSentenceEntities(): List<SentenceEntity> = map { it.toSentenceEntity() }
fun List<ParagraphItem>.toParagraphEntities(): List<ParagraphEntity> = map { it.toParagraphEntity() }
