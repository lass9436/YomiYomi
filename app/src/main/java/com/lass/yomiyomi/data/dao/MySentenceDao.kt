package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.MySentence

@Dao
interface MySentenceDao {
    
    // 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentence(mySentence: MySentence): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mySentences: List<MySentence>)

    @Update
    suspend fun updateSentence(mySentence: MySentence)

    @Delete
    suspend fun deleteSentence(mySentence: MySentence)

    @Query("DELETE FROM sentence WHERE id = :id")
    suspend fun deleteSentenceById(id: Int)

    @Query("DELETE FROM sentence WHERE paragraphId = :paragraphId")
    suspend fun deleteSentencesByParagraphId(paragraphId: String)

    // 조회
    @Query("SELECT * FROM sentence WHERE id = :id")
    suspend fun getSentenceById(id: Int): MySentence?

    @Query("SELECT * FROM sentence ORDER BY createdAt DESC")
    suspend fun getAllSentences(): List<MySentence>

    // 문단별 조회
    @Query("SELECT * FROM sentence WHERE paragraphId = :paragraphId ORDER BY orderInParagraph")
    suspend fun getSentencesByParagraph(paragraphId: String): List<MySentence>

    // 개별 문장들만 조회 (문단에 속하지 않은)
    @Query("SELECT * FROM sentence WHERE paragraphId IS NULL ORDER BY createdAt DESC")
    suspend fun getIndividualSentences(): List<MySentence>

    // 카테고리별 조회
    @Query("SELECT * FROM sentence WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getSentencesByCategory(category: String): List<MySentence>

    // 검색
    @Query("""
        SELECT * FROM sentence 
        WHERE japanese LIKE '%' || :query || '%' 
        OR korean LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchSentences(query: String): List<MySentence>

    // 학습 관련
    @Query("UPDATE sentence SET learningProgress = :progress, reviewCount = reviewCount + 1, lastReviewedAt = :timestamp WHERE id = :id")
    suspend fun updateLearningProgress(id: Int, progress: Float, timestamp: Long)

    // 통계용 - 문단별 문장 개수
    @Query("SELECT paragraphId, COUNT(*) as count FROM sentence WHERE paragraphId IS NOT NULL GROUP BY paragraphId")
    suspend fun getSentenceCountsByParagraph(): List<MySentenceCountByParagraph>

    @Query("SELECT COUNT(*) FROM sentence")
    suspend fun getTotalSentenceCount(): Int
}

// GROUP BY 결과용 데이터 클래스
data class MySentenceCountByParagraph(
    val paragraphId: String,
    val count: Int
) 
