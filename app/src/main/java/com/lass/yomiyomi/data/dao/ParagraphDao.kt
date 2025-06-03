package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.ParagraphEntity

@Dao
interface ParagraphDao {
    
    // 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParagraph(paragraph: ParagraphEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paragraphs: List<ParagraphEntity>)

    @Update
    suspend fun updateParagraph(paragraph: ParagraphEntity)

    @Delete
    suspend fun deleteParagraph(paragraph: ParagraphEntity)

    @Query("DELETE FROM paragraph WHERE paragraphId = :paragraphId")
    suspend fun deleteParagraphById(paragraphId: String)

    // 조회
    @Query("SELECT * FROM paragraph WHERE paragraphId = :paragraphId")
    suspend fun getParagraphById(paragraphId: String): ParagraphEntity?

    @Query("SELECT * FROM paragraph ORDER BY createdAt DESC")
    suspend fun getAllParagraphs(): List<ParagraphEntity>

    // 카테고리별 조회
    @Query("SELECT * FROM paragraph WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getParagraphsByCategory(category: String): List<ParagraphEntity>

    // 검색
    @Query("""
        SELECT * FROM paragraph 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchParagraphs(query: String): List<ParagraphEntity>

    // 문장 개수와 함께 조회 (LEFT JOIN 사용)
    @Query("""
        SELECT p.*, COUNT(s.id) as actualSentenceCount
        FROM paragraph p
        LEFT JOIN sentence s ON p.paragraphId = s.paragraphId
        GROUP BY p.paragraphId
        ORDER BY p.createdAt DESC
    """)
    suspend fun getParagraphsWithSentenceCounts(): List<ParagraphWithCount>

    // 통계용
    @Query("SELECT COUNT(*) FROM paragraph")
    suspend fun getTotalParagraphCount(): Int
}

// JOIN 결과용 데이터 클래스
data class ParagraphWithCount(
    val paragraphId: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val totalSentences: Int,
    val createdAt: Long,
    val actualSentenceCount: Int
) 