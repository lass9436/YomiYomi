package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.MyParagraph

@Dao
interface MyParagraphDao {
    
    // 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParagraph(myParagraph: MyParagraph): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(myParagraphs: List<MyParagraph>)

    @Update
    suspend fun updateParagraph(myParagraph: MyParagraph)

    @Delete
    suspend fun deleteParagraph(myParagraph: MyParagraph)

    @Query("DELETE FROM paragraph WHERE paragraphId = :paragraphId")
    suspend fun deleteParagraphById(paragraphId: String)

    // 조회
    @Query("SELECT * FROM paragraph WHERE paragraphId = :paragraphId")
    suspend fun getParagraphById(paragraphId: String): MyParagraph?

    @Query("SELECT * FROM paragraph ORDER BY createdAt DESC")
    suspend fun getAllParagraphs(): List<MyParagraph>

    // 카테고리별 조회
    @Query("SELECT * FROM paragraph WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getParagraphsByCategory(category: String): List<MyParagraph>

    // 🔥 레벨별 조회 추가
    @Query("SELECT * FROM paragraph WHERE level = :level ORDER BY createdAt DESC")
    suspend fun getParagraphsByLevel(level: String): List<MyParagraph>

    // 🔥 랜덤 문단 가져오기
    @Query("SELECT * FROM paragraph ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomParagraph(): MyParagraph?

    // 🔥 레벨별 랜덤 문단 가져오기
    @Query("SELECT * FROM paragraph WHERE (:level = 'ALL' OR level = :level) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomParagraphByLevel(level: String?): MyParagraph?

    // 검색
    @Query("""
        SELECT * FROM paragraph 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchParagraphs(query: String): List<MyParagraph>

    // 문장 개수와 함께 조회 (LEFT JOIN 사용)
    @Query("""
        SELECT p.*, COUNT(s.id) as actualSentenceCount
        FROM paragraph p
        LEFT JOIN sentence s ON p.paragraphId = s.paragraphId
        GROUP BY p.paragraphId
        ORDER BY p.createdAt DESC
    """)
    suspend fun getParagraphsWithSentenceCounts(): List<MyParagraphWithCount>

    // 통계용
    @Query("SELECT COUNT(*) FROM paragraph")
    suspend fun getTotalParagraphCount(): Int
}

// JOIN 결과용 데이터 클래스
data class MyParagraphWithCount(
    val paragraphId: String,
    val title: String,
    val description: String,
    val category: String,
    val level: String,
    val totalSentences: Int,
    val createdAt: Long,
    val actualSentenceCount: Int
) 
