package com.lass.yomiyomi.data.repository

import android.content.Context
import androidx.room.Transaction
import com.lass.yomiyomi.data.dao.MyParagraphWithCount
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.database.MyParagraphDataImporter
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphItems
import com.lass.yomiyomi.domain.model.mapper.toParagraphEntity
import com.lass.yomiyomi.domain.model.constant.Level

class MyParagraphRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val paragraphDao = database.myParagraphDao()
    private val sentenceDao = database.mySentenceDao()

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun importParagraphData(context: Context) {
        val paragraphList = MyParagraphDataImporter.importParagraphsFromCsv(context)
        paragraphDao.insertAll(paragraphList)
    }

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

    @Transaction
    suspend fun deleteParagraph(paragraphItem: ParagraphItem) {
        // 관련 문장들도 함께 삭제
        sentenceDao.deleteSentencesByParagraphId(paragraphItem.paragraphId)
        paragraphDao.deleteParagraph(paragraphItem.toParagraphEntity())
    }

    @Transaction
    suspend fun deleteParagraphById(paragraphId: Int) {
        // 관련 문장들도 함께 삭제
        sentenceDao.deleteSentencesByParagraphId(paragraphId)
        paragraphDao.deleteParagraphById(paragraphId)
    }

    // 조회
    suspend fun getParagraphById(paragraphId: Int): ParagraphItem? {
        return paragraphDao.getParagraphById(paragraphId)?.toParagraphItem()
    }

    suspend fun getAllParagraphs(): List<ParagraphItem> {
        return paragraphDao.getAllParagraphs().toParagraphItems()
    }

    // 카테고리별 조회
    suspend fun getParagraphsByCategory(category: String): List<ParagraphItem> {
        return paragraphDao.getParagraphsByCategory(category).toParagraphItems()
    }

    // 🔥 레벨별 조회 추가
    suspend fun getParagraphsByLevel(level: String): List<ParagraphItem> {
        return paragraphDao.getParagraphsByLevel(level).toParagraphItems()
    }

    // 🔥 랜덤 문단 가져오기
    suspend fun getRandomParagraph(): ParagraphItem? {
        return paragraphDao.getRandomParagraph()?.toParagraphItem()
    }

    // 🔥 레벨별 랜덤 문단 가져오기
    suspend fun getRandomParagraphByLevel(level: String?): ParagraphItem? {
        return paragraphDao.getRandomParagraphByLevel(level)?.toParagraphItem()
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
private fun MyParagraphWithCount.toParagraphItem(): ParagraphItem = ParagraphItem(
    paragraphId = paragraphId,
    title = title,
    description = description,
    category = category,
    level = Level.values().find { it.value == level } ?: Level.N5,
    totalSentences = totalSentences,
    actualSentenceCount = actualSentenceCount,
    createdAt = createdAt
) 
