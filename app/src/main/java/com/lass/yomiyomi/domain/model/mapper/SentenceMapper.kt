package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.MySentence
import com.lass.yomiyomi.data.model.MyParagraph
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.Level

// Entity -> Domain Model (조회용)
// SentenceEntity -> SentenceItem
fun MySentence.toSentenceItem(): SentenceItem = SentenceItem(
    id = id,
    japanese = japanese,
    korean = korean,
    paragraphId = paragraphId,
    orderInParagraph = orderInParagraph,
    category = category,
    level = Level.values().find { it.value == level } ?: Level.N5,
    learningProgress = learningProgress,
    reviewCount = reviewCount,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt
)

// ParagraphEntity -> ParagraphItem
fun MyParagraph.toParagraphItem(actualSentenceCount: Int = 0): ParagraphItem = ParagraphItem(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    level = Level.values().find { it.value == level } ?: Level.N5,
    totalSentences = totalSentences,
    actualSentenceCount = actualSentenceCount,
    createdAt = createdAt
)

// Domain Model -> Entity (CRUD용)
// SentenceItem -> SentenceEntity
fun SentenceItem.toSentenceEntity(): MySentence = MySentence(
    id = id,
    japanese = japanese,
    korean = korean,
    paragraphId = paragraphId,
    orderInParagraph = orderInParagraph,
    category = category,
    level = level.value ?: "N5",
    learningProgress = learningProgress,
    reviewCount = reviewCount,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt
)

// ParagraphItem -> ParagraphEntity
fun ParagraphItem.toParagraphEntity(): MyParagraph = MyParagraph(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    level = level.value ?: "N5",
    totalSentences = totalSentences,
    createdAt = createdAt
)

// List 변환 함수들 (Entity -> Domain)
fun List<MySentence>.toSentenceItems(): List<SentenceItem> = map { it.toSentenceItem() }
fun List<MyParagraph>.toParagraphItems(sentenceCounts: Map<String, Int> = emptyMap()): List<ParagraphItem> =
    map { it.toParagraphItem(sentenceCounts[it.paragraphId] ?: 0) }

// List 변환 함수들 (Domain -> Entity)
fun List<SentenceItem>.toSentenceEntities(): List<MySentence> = map { it.toSentenceEntity() }
fun List<ParagraphItem>.toParagraphEntities(): List<MyParagraph> = map { it.toParagraphEntity() }
