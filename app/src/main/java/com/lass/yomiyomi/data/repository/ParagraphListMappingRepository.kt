package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListMappingItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphListItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphListMapping

class ParagraphListMappingRepository(context: Context) {
    private val mapDao = AppDatabase.getInstance(context).paragraphListMappingDao()

    suspend fun addMapping(item: ParagraphListMappingItem) =
        mapDao.insertMapping(item.toParagraphListMapping())

    suspend fun removeMapping(listId: Int, paragraphId: Int) =
        mapDao.deleteMappingByIds(listId, paragraphId)

    suspend fun removeMappingsByList(listId: Int) =
        mapDao.deleteMappingsByListId(listId)

    suspend fun removeMappingsByParagraph(paragraphId: Int) =
        mapDao.deleteMappingsByParagraphId(paragraphId)

    suspend fun getParagraphsInList(listId: Int): List<ParagraphItem> =
        mapDao.getParagraphsInList(listId).map { it.toParagraphItem() }

    suspend fun getListsByParagraph(paragraphId: Int): List<ParagraphListItem> =
        mapDao.getListsByParagraphId(paragraphId).map { it.toParagraphListItem() }
}
