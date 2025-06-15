package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.ParagraphList

class ParagraphListRepository(context: Context) {
    private val listDao = AppDatabase.getInstance(context).paragraphListDao()

    suspend fun createList(list: ParagraphList) = listDao.insertParagraphList(list)
    suspend fun updateList(list: ParagraphList) = listDao.updateParagraphList(list)
    suspend fun deleteList(listId: Int) = listDao.deleteParagraphListById(listId)
    suspend fun getListById(listId: Int) = listDao.getParagraphListById(listId)
    suspend fun getAllLists() = listDao.getAllParagraphLists()
}
