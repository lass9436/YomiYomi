package com.lass.yomiyomi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.lass.yomiyomi.data.model.MyWord

@Dao
interface MyWordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyWord(myWord: MyWord) // 내 단어 추가

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(myWords: List<MyWord>) // 내 단어들을 한번에 삽입

    @Query("SELECT * FROM my_word")
    suspend fun getAllMyWords(): List<MyWord> // 전체 내 단어 조회

    @Query("SELECT * FROM my_word WHERE (:level = 'ALL' OR level = :level)")
    suspend fun getAllMyWordsByLevel(level: String): List<MyWord> // 특정 수준(Level) 내 단어 조회

    @Query("SELECT * FROM my_word ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomMyWord(): MyWord? // 랜덤 내 단어 하나 조회

    @Query("SELECT * FROM my_word WHERE (:level = 'ALL' OR level = :level) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomMyWordByLevel(level: String?): MyWord? // 특정 수준(Level)에서 랜덤 내 단어 하나 조회

    @Query("SELECT * FROM my_word WHERE word LIKE '%' || :searchQuery || '%' OR meaning LIKE '%' || :searchQuery || '%'")
    suspend fun searchMyWords(searchQuery: String): List<MyWord> // 내 단어 검색

    @Query("SELECT * FROM my_word WHERE id = :id")
    suspend fun getMyWordById(id: Int): MyWord? // ID로 내 단어 조회

    @Delete
    suspend fun deleteMyWord(myWord: MyWord) // 내 단어 삭제

    @Query("DELETE FROM my_word WHERE id = :id")
    suspend fun deleteMyWordById(id: Int) // ID로 내 단어 삭제

    // 학습 모드용 - 가중치 상위 5개
    @Query("""
        SELECT * FROM my_word 
        WHERE (:level = 'ALL' OR level = :level)
        ORDER BY learningWeight DESC 
        LIMIT 5
    """)
    suspend fun getTopPriorityMyWords(level: String): List<MyWord>

    // 학습 모드용 - 오답 보기용 랜덤 15개
    @Query("""
        SELECT * FROM my_word 
        WHERE (:level = 'ALL' OR level = :level)
        AND id NOT IN (
            SELECT id FROM my_word 
            WHERE (:level = 'ALL' OR level = :level)
            ORDER BY learningWeight DESC 
            LIMIT 5
        )
        ORDER BY RANDOM()
        LIMIT 15
    """)
    suspend fun getRandomMyWordDistractors(level: String): List<MyWord>

    // 가중치와 학습 시간 업데이트
    @Query("UPDATE my_word SET learningWeight = :weight, timestamp = :timestamp WHERE id = :wordId")
    suspend fun updateMyWordLearningStatus(wordId: Int, weight: Float, timestamp: Long)
} 