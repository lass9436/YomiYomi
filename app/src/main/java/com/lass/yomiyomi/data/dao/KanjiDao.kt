package com.lass.yomiyomi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lass.yomiyomi.data.model.Kanji

@Dao
interface KanjiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(kanjiList: List<Kanji>)

    @Query("SELECT * FROM kanji")
    suspend fun getAllKanji(): List<Kanji>

    @Query("SELECT * FROM kanji WHERE (:level = 'ALL' OR level = :level)")
    suspend fun getAllKanjiByLevel(level: String): List<Kanji>

    @Query("SELECT * FROM kanji ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomKanji(): Kanji

    @Query("SELECT * FROM kanji WHERE (:level = 'ALL' OR level = :level) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomKanjiByLevel(level: String): Kanji?

    // 학습 모드용 - 가중치 상위 5개
    @Query("""
        SELECT * FROM kanji 
        WHERE (:level = 'ALL' OR level = :level)
        ORDER BY learningWeight DESC 
        LIMIT 5
    """)
    suspend fun getTopPriorityKanji(level: String): List<Kanji>

    // 학습 모드용 - 오답 보기용 랜덤 15개
    @Query("""
        SELECT * FROM kanji 
        WHERE (:level = 'ALL' OR level = :level)
        AND id NOT IN (
            SELECT id FROM kanji 
            WHERE (:level = 'ALL' OR level = :level)
            ORDER BY learningWeight DESC 
            LIMIT 5
        )
        ORDER BY RANDOM()
        LIMIT 15
    """)
    suspend fun getRandomDistractors(level: String): List<Kanji>

    // 가중치와 학습 시간 업데이트
    @Query("UPDATE kanji SET learningWeight = :weight, timestamp = :timestamp WHERE id = :kanjiId")
    suspend fun updateKanjiLearningStatus(kanjiId: Int, weight: Float, timestamp: Long)
}