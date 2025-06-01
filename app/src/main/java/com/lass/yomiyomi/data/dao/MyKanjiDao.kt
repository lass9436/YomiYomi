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
} 