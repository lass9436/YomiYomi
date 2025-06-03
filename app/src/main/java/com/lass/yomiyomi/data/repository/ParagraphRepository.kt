package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.dao.ParagraphWithCount
import com.lass.yomiyomi.domain.model.ParagraphItem
import com.lass.yomiyomi.domain.model.toParagraphItem
import com.lass.yomiyomi.domain.model.toParagraphItems
import com.lass.yomiyomi.domain.model.toParagraphEntity

class ParagraphRepository(private val context: Context) {
    private val paragraphDao = AppDatabase.getInstance(context).paragraphDao()

    // 기본 CRUD
    suspend fun insertParagraph(paragraphItem: ParagraphItem): Long {
        return paragraphDao.insertParagraph(paragraphItem.toParagraphEntity())
    }

    suspend fun insertAll(paragraphItems: List<ParagraphItem>) {
        paragraphDao.insertAll(paragraphItems.map { it.toParagraphEntity() })
    }

    suspend fun updateParagraph(paragraphItem: ParagraphItem) {
        paragraphDao.updateParagraph(paragraphItem.toParagraphEntity())
    }

    suspend fun deleteParagraph(paragraphItem: ParagraphItem) {
        paragraphDao.deleteParagraph(paragraphItem.toParagraphEntity())
    }

    suspend fun deleteParagraphById(paragraphId: String) {
        paragraphDao.deleteParagraphById(paragraphId)
    }

    // 조회
    suspend fun getParagraphById(paragraphId: String): ParagraphItem? {
        return paragraphDao.getParagraphById(paragraphId)?.toParagraphItem()
    }

    suspend fun getAllParagraphs(): List<ParagraphItem> {
        return paragraphDao.getAllParagraphs().toParagraphItems()
    }

    // 카테고리별 조회
    suspend fun getParagraphsByCategory(category: String): List<ParagraphItem> {
        return paragraphDao.getParagraphsByCategory(category).toParagraphItems()
    }

    // 검색
    suspend fun searchParagraphs(query: String): List<ParagraphItem> {
        return paragraphDao.searchParagraphs(query).toParagraphItems()
    }

    // 문장 개수와 함께 조회
    suspend fun getParagraphsWithSentenceCounts(): List<ParagraphItem> {
        return paragraphDao.getParagraphsWithSentenceCounts().map { it.toParagraphItem() }
    }

    // 통계
    suspend fun getTotalParagraphCount(): Int {
        return paragraphDao.getTotalParagraphCount()
    }
}

// ParagraphWithCount -> ParagraphItem 변환 확장 함수
private fun ParagraphWithCount.toParagraphItem(): ParagraphItem = ParagraphItem(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    totalSentences = totalSentences,
    actualSentenceCount = actualSentenceCount,
    createdAt = createdAt
) 