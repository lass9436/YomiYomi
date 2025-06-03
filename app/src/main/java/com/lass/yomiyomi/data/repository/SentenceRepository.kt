package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.database.SentenceDataImporter
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.mapper.toSentenceItem
import com.lass.yomiyomi.domain.model.mapper.toSentenceItems
import com.lass.yomiyomi.domain.model.mapper.toSentenceEntity

class SentenceRepository(private val context: Context) {
    private val sentenceDao = AppDatabase.getInstance(context).sentenceDao()

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun importSentenceData(context: Context) {
        val sentenceList = SentenceDataImporter.importSentencesFromCsv(context)
        sentenceDao.insertAll(sentenceList)
    }

    // 기본 CRUD
    suspend fun insertSentence(sentenceItem: SentenceItem): Long {
        return sentenceDao.insertSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun insertAll(sentenceItems: List<SentenceItem>) {
        sentenceDao.insertAll(sentenceItems.map { it.toSentenceEntity() })
    }

    suspend fun updateSentence(sentenceItem: SentenceItem) {
        sentenceDao.updateSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun deleteSentence(sentenceItem: SentenceItem) {
        sentenceDao.deleteSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun deleteSentenceById(id: Int) {
        sentenceDao.deleteSentenceById(id)
    }

    // 조회
    suspend fun getSentenceById(id: Int): SentenceItem? {
        return sentenceDao.getSentenceById(id)?.toSentenceItem()
    }

    suspend fun getAllSentences(): List<SentenceItem> {
        return sentenceDao.getAllSentences().toSentenceItems()
    }

    // 문단별 조회
    suspend fun getSentencesByParagraph(paragraphId: String): List<SentenceItem> {
        return sentenceDao.getSentencesByParagraph(paragraphId).toSentenceItems()
    }

    // 개별 문장들만 조회 (문단에 속하지 않은)
    suspend fun getIndividualSentences(): List<SentenceItem> {
        return sentenceDao.getIndividualSentences().toSentenceItems()
    }

    // 카테고리별 조회
    suspend fun getSentencesByCategory(category: String): List<SentenceItem> {
        return sentenceDao.getSentencesByCategory(category).toSentenceItems()
    }

    // 검색
    suspend fun searchSentences(query: String): List<SentenceItem> {
        return sentenceDao.searchSentences(query).toSentenceItems()
    }

    // 학습 관련
    suspend fun updateLearningProgress(id: Int, progress: Float) {
        val timestamp = System.currentTimeMillis()
        sentenceDao.updateLearningProgress(id, progress, timestamp)
    }

    // 통계
    suspend fun getSentenceCountsByParagraph(): Map<String, Int> {
        return sentenceDao.getSentenceCountsByParagraph()
            .associate { it.paragraphId to it.count }
    }

    suspend fun getTotalSentenceCount(): Int {
        return sentenceDao.getTotalSentenceCount()
    }

    // 카테고리와 난이도 목록 가져오기 (동적)
    suspend fun getDistinctCategories(): List<String> {
        return sentenceDao.getAllSentences().map { it.category }.distinct().sorted()
    }

    suspend fun getDistinctDifficulties(): List<String> {
        return sentenceDao.getAllSentences().map { it.difficulty }.distinct().sorted()
    }
} 
