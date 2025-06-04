package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.MyKanji

@Dao
interface MyKanjiDao {
    @Query("SELECT * FROM my_kanji ORDER BY id DESC")
    suspend fun getAllMyKanji(): List<MyKanji>

    @Query("SELECT * FROM my_kanji WHERE level = :level ORDER BY id DESC")
    suspend fun getMyKanjiByLevel(level: String): List<MyKanji>

    @Query("SELECT * FROM my_kanji WHERE kanji LIKE '%' || :query || '%' OR onyomi LIKE '%' || :query || '%' OR kunyomi LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchMyKanji(query: String): List<MyKanji>

    @Query("SELECT * FROM my_kanji WHERE level = :level AND (kanji LIKE '%' || :query || '%' OR onyomi LIKE '%' || :query || '%' OR kunyomi LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%') ORDER BY id DESC")
    suspend fun searchMyKanjiByLevel(level: String, query: String): List<MyKanji>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyKanji(myKanji: MyKanji): Long

    @Update
    suspend fun updateMyKanji(myKanji: MyKanji)

    @Delete
    suspend fun deleteMyKanji(myKanji: MyKanji)

    @Query("DELETE FROM my_kanji WHERE id = :id")
    suspend fun deleteMyKanjiById(id: Int)

    @Query("SELECT * FROM my_kanji WHERE id = :id")
    suspend fun getMyKanjiById(id: Int): MyKanji?

    // 학습 모드용 - 가중치 상위 5개
    @Query("""
        SELECT * FROM my_kanji 
        WHERE (:level = 'ALL' OR level = :level)
        ORDER BY learningWeight DESC 
        LIMIT 5
    """)
    suspend fun getTopPriorityMyKanji(level: String): List<MyKanji>

    // 학습 모드용 - 오답 보기용 랜덤 15개
    @Query("""
        SELECT * FROM my_kanji 
        WHERE (:level = 'ALL' OR level = :level)
        AND id NOT IN (
            SELECT id FROM my_kanji 
            WHERE (:level = 'ALL' OR level = :level)
            ORDER BY learningWeight DESC 
            LIMIT 5
        )
        ORDER BY RANDOM()
        LIMIT 15
    """)
    suspend fun getRandomMyKanjiDistractors(level: String): List<MyKanji>

    // 가중치와 학습 시간 업데이트
    @Query("UPDATE my_kanji SET learningWeight = :weight, timestamp = :timestamp WHERE id = :kanjiId")
    suspend fun updateMyKanjiLearningStatus(kanjiId: Int, weight: Float, timestamp: Long)

    // 전체 내 한자 개수 조회
    @Query("SELECT COUNT(*) FROM my_kanji")
    suspend fun getMyKanjiCount(): Int
} 