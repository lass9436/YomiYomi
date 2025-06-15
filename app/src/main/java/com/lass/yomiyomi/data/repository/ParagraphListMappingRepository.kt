package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.ParagraphListMapping
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListMappingItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphListItem
import com.lass.yomiyomi.domain.model.mapper.toParagraphListMapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParagraphListMappingRepository(context: Context) {
    private val mapDao = AppDatabase.getInstance(context).paragraphListMappingDao()

    suspend fun addMapping(paragraphId: Int, listId: Int) {
        val mapping = ParagraphListMapping(
            listId = listId,
            paragraphId = paragraphId,
            sortOrder = 0,
            createdAt = System.currentTimeMillis()
        )
        mapDao.insertMapping(mapping)
    }

    suspend fun removeMapping(listId: Int, paragraphId: Int) {
        mapDao.deleteMappingByIds(listId, paragraphId)
    }

    suspend fun removeMappingsByList(listId: Int) {
        mapDao.deleteMappingsByListId(listId)
    }

    suspend fun removeMappingsByParagraph(paragraphId: Int) {
        mapDao.deleteMappingsByParagraphId(paragraphId)
    }

    suspend fun getParagraphsInList(listId: Int): List<ParagraphItem> {
        return mapDao.getParagraphsInList(listId).map { it.toParagraphItem() }
    }

    suspend fun getListsByParagraph(paragraphId: Int): List<ParagraphListItem> {
        return mapDao.getListsByParagraphId(paragraphId).map { it.toParagraphListItem() }
    }

    // 모든 매핑 정보를 가져옴
    suspend fun getAllMappings(): List<ParagraphListMapping> {
        return withContext(Dispatchers.IO) {
            mapDao.getAllMappings()
        }
    }
}
