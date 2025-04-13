package com.lass.yomiyomi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lass.yomiyomi.data.model.Kanji

@Dao
interface KanjiDao {
    @Insert
    suspend fun insertAll(kanjiList: List<Kanji>) // Kanji 리스트 삽입

    @Query("SELECT * FROM kanji_table")
    suspend fun getAllKanji(): List<Kanji> // 전체 Kanji를 가져옴
}