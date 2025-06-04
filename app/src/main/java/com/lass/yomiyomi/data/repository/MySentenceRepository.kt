package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.database.MySentenceDataImporter
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.mapper.toSentenceItem
import com.lass.yomiyomi.domain.model.mapper.toSentenceItems
import com.lass.yomiyomi.domain.model.mapper.toSentenceEntity

class MySentenceRepository(private val context: Context) {
    private val mySentenceDao = AppDatabase.getInstance(context).mySentenceDao()

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun importSentenceData(context: Context) {
        val sentenceList = MySentenceDataImporter.importSentencesFromCsv(context)
        mySentenceDao.insertAll(sentenceList)
    }

    // 기본 CRUD
    suspend fun insertSentence(sentenceItem: SentenceItem): Long {
        return mySentenceDao.insertSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun insertAll(sentenceItems: List<SentenceItem>) {
        mySentenceDao.insertAll(sentenceItems.map { it.toSentenceEntity() })
    }

    suspend fun updateSentence(sentenceItem: SentenceItem) {
        mySentenceDao.updateSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun deleteSentence(sentenceItem: SentenceItem) {
        mySentenceDao.deleteSentence(sentenceItem.toSentenceEntity())
    }

    suspend fun deleteSentenceById(id: Int) {
        mySentenceDao.deleteSentenceById(id)
    }

    // 조회
    suspend fun getSentenceById(id: Int): SentenceItem? {
        return mySentenceDao.getSentenceById(id)?.toSentenceItem()
    }

    suspend fun getAllSentences(): List<SentenceItem> {
        return mySentenceDao.getAllSentences().toSentenceItems()
    }

    // 문단별 조회
    suspend fun getSentencesByParagraph(paragraphId: String): List<SentenceItem> {
        return mySentenceDao.getSentencesByParagraph(paragraphId).toSentenceItems()
    }

    // 개별 문장들만 조회 (문단에 속하지 않은)
    suspend fun getIndividualSentences(): List<SentenceItem> {
        return mySentenceDao.getIndividualSentences().toSentenceItems()
    }

    // 카테고리별 조회
    suspend fun getSentencesByCategory(category: String): List<SentenceItem> {
        return mySentenceDao.getSentencesByCategory(category).toSentenceItems()
    }

    // 🔥 레벨별 조회 추가
    suspend fun getSentencesByLevel(level: String): List<SentenceItem> {
        return mySentenceDao.getSentencesByLevel(level).toSentenceItems()
    }

    // 🔥 개별 문장들을 레벨별로 조회 (문단에 속하지 않은)
    suspend fun getIndividualSentencesByLevel(level: String): List<SentenceItem> {
        return mySentenceDao.getIndividualSentencesByLevel(level).toSentenceItems()
    }

    // 🔥 랜덤 문장 가져오기 (개별 문장만)
    suspend fun getRandomIndividualSentence(): SentenceItem? {
        return mySentenceDao.getRandomIndividualSentence()?.toSentenceItem()
    }

    // 🔥 레벨별 랜덤 문장 가져오기 (개별 문장만)
    suspend fun getRandomIndividualSentenceByLevel(level: String?): SentenceItem? {
        return mySentenceDao.getRandomIndividualSentenceByLevel(level)?.toSentenceItem()
    }

    // ViewModel 호환성을 위한 메소드
    suspend fun getRandomSentenceByLevel(level: String?): SentenceItem? {
        return getRandomIndividualSentenceByLevel(level)
    }

    // 검색
    suspend fun searchSentences(query: String): List<SentenceItem> {
        return mySentenceDao.searchSentences(query).toSentenceItems()
    }

    // 학습 관련
    suspend fun updateLearningProgress(id: Int, progress: Float) {
        val timestamp = System.currentTimeMillis()
        mySentenceDao.updateLearningProgress(id, progress, timestamp)
    }

    // 통계
    suspend fun getSentenceCountsByParagraph(): Map<String, Int> {
        return mySentenceDao.getSentenceCountsByParagraph()
            .associate { it.paragraphId to it.count }
    }

    suspend fun getTotalSentenceCount(): Int {
        return mySentenceDao.getTotalSentenceCount()
    }

    // 카테고리와 레벨 목록 가져오기 (동적)
    suspend fun getDistinctCategories(): List<String> {
        return mySentenceDao.getAllSentences().map { it.category }.distinct().sorted()
    }

    suspend fun getDistinctLevels(): List<String> {
        return mySentenceDao.getAllSentences().map { it.level }.distinct().sorted()
    }
} 
