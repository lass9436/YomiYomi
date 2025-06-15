package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.ParagraphListMapping

class ParagraphListMappingRepository(context: Context) {
    private val mapDao = AppDatabase.getInstance(context).paragraphListMappingDao()

    suspend fun addMapping(mapping: ParagraphListMapping) = mapDao.insertMapping(mapping)
    suspend fun removeMapping(listId: Int, paragraphId: Int) = mapDao.deleteMappingByIds(listId, paragraphId)
    suspend fun removeMappingsByList(listId: Int) = mapDao.deleteMappingsByListId(listId)
    suspend fun removeMappingsByParagraph(paragraphId: Int) = mapDao.deleteMappingsByParagraphId(paragraphId)

    suspend fun getParagraphsInList(listId: Int) = mapDao.getParagraphsInList(listId)
    suspend fun getListsByParagraph(paragraphId: Int) = mapDao.getListsByParagraphId(paragraphId)
}
