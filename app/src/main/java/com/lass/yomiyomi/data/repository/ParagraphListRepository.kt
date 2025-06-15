package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphList
import com.lass.yomiyomi.domain.model.mapper.toParagraphListItem

class ParagraphListRepository(context: Context) {
    private val listDao = AppDatabase.getInstance(context).paragraphListDao()

    suspend fun createList(item: ParagraphListItem): Long {
        android.util.Log.d("YomiYomi", "Repository: Creating list: ${item.name}")
        val result = listDao.insertParagraphList(item.toParagraphList())
        android.util.Log.d("YomiYomi", "Repository: Created list with result: $result")
        return result
    }

    suspend fun updateList(item: ParagraphListItem) {
        android.util.Log.d("YomiYomi", "Repository: Updating list: ${item.listId}")
        listDao.updateParagraphList(item.toParagraphList())
    }

    suspend fun deleteList(listId: Int) {
        android.util.Log.d("YomiYomi", "Repository: Deleting list: $listId")
        listDao.deleteParagraphListById(listId)
    }

    suspend fun getListById(listId: Int): ParagraphListItem? {
        android.util.Log.d("YomiYomi", "Repository: Getting list by ID: $listId")
        return listDao.getParagraphListById(listId)?.toParagraphListItem()
    }

    suspend fun getAllLists(): List<ParagraphListItem> {
        android.util.Log.d("YomiYomi", "Repository: Getting all lists")
        val lists = listDao.getAllParagraphLists().map { it.toParagraphListItem() }
        android.util.Log.d("YomiYomi", "Repository: Found ${lists.size} lists")
        return lists
    }
}
