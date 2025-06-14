package com.lass.yomiyomi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lass.yomiyomi.data.model.Word

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<Word>) // Word 데이터를 한번에 삽입

    @Query("SELECT * FROM word")
    suspend fun getAllWords(): List<Word> // 전체 단어 조회

    @Query("SELECT * FROM word WHERE (:level = 'ALL' OR level = :level)")
    suspend fun getAllWordsByLevel(level: String): List<Word> // 특정 수준(Level) 단어 조회

    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word // 랜덤 단어 하나 조회

    @Query("SELECT * FROM word WHERE (:level = 'ALL' OR level = :level) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordByLevel(level: String?): Word? // 특정 수준(Level)에서 랜덤 단어 하나 조회

    // 학습 모드용 - 가중치 상위 5개
    @Query("""
        SELECT * FROM word 
        WHERE (:level = 'ALL' OR level = :level)
        ORDER BY learningWeight DESC, RANDOM()
        LIMIT 5
    """)
    suspend fun getTopPriorityWords(level: String): List<Word>

    // 학습 모드용 - 오답 보기용 랜덤 30개
    @Query("""
        SELECT * FROM word 
        WHERE (:level = 'ALL' OR level = :level)
        ORDER BY RANDOM()
        LIMIT 30
    """)
    suspend fun getRandomDistractors(level: String): List<Word>

    // 가중치와 학습 시간 업데이트
    @Query("UPDATE word SET learningWeight = :weight, timestamp = :timestamp WHERE id = :wordId")
    suspend fun updateWordLearningStatus(wordId: Int, weight: Float, timestamp: Long)
}
