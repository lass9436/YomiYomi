package com.lass.yomiyomi.data.dao

import androidx.room.*
import com.lass.yomiyomi.data.model.ParagraphListMapping
import com.lass.yomiyomi.data.model.MyParagraph
import com.lass.yomiyomi.data.model.ParagraphList

@Dao
interface ParagraphListMappingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: ParagraphListMapping)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMappings(mappings: List<ParagraphListMapping>)

    @Delete
    suspend fun deleteMapping(mapping: ParagraphListMapping)

    @Query("DELETE FROM paragraph_list_mapping WHERE listId = :listId AND paragraphId = :paragraphId")
    suspend fun deleteMappingByIds(listId: Int, paragraphId: Int)

    @Query("DELETE FROM paragraph_list_mapping WHERE listId = :listId")
    suspend fun deleteMappingsByListId(listId: Int)

    @Query("DELETE FROM paragraph_list_mapping WHERE paragraphId = :paragraphId")
    suspend fun deleteMappingsByParagraphId(paragraphId: Int)

    // 리스트에 속한 문단 조회
    @Transaction
    @Query("""
        SELECT p.* FROM paragraph_list_mapping m
        INNER JOIN paragraph p ON m.paragraphId = p.paragraphId
        WHERE m.listId = :listId
        ORDER BY m.sortOrder ASC, p.createdAt DESC
    """)
    suspend fun getParagraphsInList(listId: Int): List<MyParagraph>

    // 문단이 포함된 리스트 조회
    @Transaction
    @Query("""
        SELECT l.* FROM paragraph_list_mapping m
        INNER JOIN paragraph_list l ON m.listId = l.listId
        WHERE m.paragraphId = :paragraphId
        ORDER BY l.createdAt DESC
    """)
    suspend fun getListsByParagraphId(paragraphId: Int): List<ParagraphList>

    // 모든 매핑 정보 조회
    @Query("SELECT * FROM paragraph_list_mapping")
    suspend fun getAllMappings(): List<ParagraphListMapping>
}
