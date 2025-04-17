package com.lass.yomiyomi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level

@Dao
interface KanjiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kanjiList: List<Kanji>) // 중복 데이터 대체

    @Query("SELECT * FROM kanji")
    suspend fun getAllKanji(): List<Kanji> // 전체 Kanji를 가져옴

    @Query("SELECT * FROM kanji ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomKanji(): Kanji

    @Query("SELECT * FROM kanji WHERE (:level = 'ALL' OR level = :level) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomKanjiByLevel(level: String?): List<Kanji>

}