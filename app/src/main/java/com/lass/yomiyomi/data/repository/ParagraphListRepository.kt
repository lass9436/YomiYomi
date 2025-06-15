package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphList
import com.lass.yomiyomi.domain.model.mapper.toParagraphListItem

class ParagraphListRepository(context: Context) {
    private val listDao = AppDatabase.getInstance(context).paragraphListDao()

    suspend fun createList(item: ParagraphListItem): Long =
        listDao.insertParagraphList(item.toParagraphList())

    suspend fun updateList(item: ParagraphListItem) =
        listDao.updateParagraphList(item.toParagraphList())

    suspend fun deleteList(listId: Int) =
        listDao.deleteParagraphListById(listId)

    suspend fun getListById(listId: Int): ParagraphListItem? =
        listDao.getParagraphListById(listId)?.toParagraphListItem()

    suspend fun getAllLists(): List<ParagraphListItem> =
        listDao.getAllParagraphLists().map { it.toParagraphListItem() }
}
