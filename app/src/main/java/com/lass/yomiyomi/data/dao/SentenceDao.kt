package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.SentenceEntity

@Dao
interface SentenceDao {
    
    // 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentence(sentence: SentenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sentences: List<SentenceEntity>)

    @Update
    suspend fun updateSentence(sentence: SentenceEntity)

    @Delete
    suspend fun deleteSentence(sentence: SentenceEntity)

    @Query("DELETE FROM sentence WHERE id = :id")
    suspend fun deleteSentenceById(id: Int)

    // 조회
    @Query("SELECT * FROM sentence WHERE id = :id")
    suspend fun getSentenceById(id: Int): SentenceEntity?

    @Query("SELECT * FROM sentence ORDER BY createdAt DESC")
    suspend fun getAllSentences(): List<SentenceEntity>

    // 문단별 조회
    @Query("SELECT * FROM sentence WHERE paragraphId = :paragraphId ORDER BY orderInParagraph")
    suspend fun getSentencesByParagraph(paragraphId: String): List<SentenceEntity>

    // 개별 문장들만 조회 (문단에 속하지 않은)
    @Query("SELECT * FROM sentence WHERE paragraphId IS NULL ORDER BY createdAt DESC")
    suspend fun getIndividualSentences(): List<SentenceEntity>

    // 카테고리별 조회
    @Query("SELECT * FROM sentence WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getSentencesByCategory(category: String): List<SentenceEntity>

    // 검색
    @Query("""
        SELECT * FROM sentence 
        WHERE japanese LIKE '%' || :query || '%' 
        OR korean LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchSentences(query: String): List<SentenceEntity>

    // 학습 관련
    @Query("UPDATE sentence SET learningProgress = :progress, reviewCount = reviewCount + 1, lastReviewedAt = :timestamp WHERE id = :id")
    suspend fun updateLearningProgress(id: Int, progress: Float, timestamp: Long)

    // 통계용 - 문단별 문장 개수
    @Query("SELECT paragraphId, COUNT(*) as count FROM sentence WHERE paragraphId IS NOT NULL GROUP BY paragraphId")
    suspend fun getSentenceCountsByParagraph(): List<SentenceCountByParagraph>

    @Query("SELECT COUNT(*) FROM sentence")
    suspend fun getTotalSentenceCount(): Int
}

// GROUP BY 결과용 데이터 클래스
data class SentenceCountByParagraph(
    val paragraphId: String,
    val count: Int
) 