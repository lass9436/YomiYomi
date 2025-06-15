package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.ParagraphList

@Dao
interface ParagraphListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParagraphList(list: ParagraphList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllParagraphLists(lists: List<ParagraphList>)

    @Update
    suspend fun updateParagraphList(list: ParagraphList)

    @Delete
    suspend fun deleteParagraphList(list: ParagraphList)

    @Query("DELETE FROM paragraph_list WHERE listId = :listId")
    suspend fun deleteParagraphListById(listId: Int)

    @Query("SELECT * FROM paragraph_list WHERE listId = :listId")
    suspend fun getParagraphListById(listId: Int): ParagraphList?

    @Query("SELECT * FROM paragraph_list ORDER BY createdAt DESC")
    suspend fun getAllParagraphLists(): List<ParagraphList>
}
