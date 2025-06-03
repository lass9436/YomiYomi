package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.Sentence
import com.lass.yomiyomi.data.model.Paragraph
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem

// Entity -> Domain Model (조회용)
// SentenceEntity -> SentenceItem
fun Sentence.toSentenceItem(): SentenceItem = SentenceItem(
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
fun Paragraph.toParagraphItem(actualSentenceCount: Int = 0): ParagraphItem = ParagraphItem(
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
fun SentenceItem.toSentenceEntity(): Sentence = Sentence(
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
fun ParagraphItem.toParagraphEntity(): Paragraph = Paragraph(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    totalSentences = totalSentences,
    createdAt = createdAt
)

// List 변환 함수들 (Entity -> Domain)
fun List<Sentence>.toSentenceItems(): List<SentenceItem> = map { it.toSentenceItem() }
fun List<Paragraph>.toParagraphItems(sentenceCounts: Map<String, Int> = emptyMap()): List<ParagraphItem> =
    map { it.toParagraphItem(sentenceCounts[it.paragraphId] ?: 0) }

// List 변환 함수들 (Domain -> Entity)
fun List<SentenceItem>.toSentenceEntities(): List<Sentence> = map { it.toSentenceEntity() }
fun List<ParagraphItem>.toParagraphEntities(): List<Paragraph> = map { it.toParagraphEntity() }
