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
        android.util.Log.d("YomiYomi", "Repository: Removing mapping - listId: $listId, paragraphId: $paragraphId")
        mapDao.deleteMappingByIds(listId, paragraphId)
    }

    suspend fun removeMappingsByList(listId: Int) {
        android.util.Log.d("YomiYomi", "Repository: Removing all mappings for list: $listId")
        mapDao.deleteMappingsByListId(listId)
    }

    suspend fun removeMappingsByParagraph(paragraphId: Int) {
        android.util.Log.d("YomiYomi", "Repository: Removing all mappings for paragraph: $paragraphId")
        mapDao.deleteMappingsByParagraphId(paragraphId)
    }

    suspend fun getParagraphsInList(listId: Int): List<ParagraphItem> {
        android.util.Log.d("YomiYomi", "Repository: Getting paragraphs in list: $listId")
        val paragraphs = mapDao.getParagraphsInList(listId).map { it.toParagraphItem() }
        android.util.Log.d("YomiYomi", "Repository: Found ${paragraphs.size} paragraphs in list $listId")
        return paragraphs
    }

    suspend fun getListsByParagraph(paragraphId: Int): List<ParagraphListItem> {
        android.util.Log.d("YomiYomi", "Repository: Getting lists for paragraph: $paragraphId")
        val lists = mapDao.getListsByParagraphId(paragraphId).map { it.toParagraphListItem() }
        android.util.Log.d("YomiYomi", "Repository: Found ${lists.size} lists for paragraph $paragraphId: ${lists.map { it.listId }}")
        return lists
    }

    // 모든 매핑 정보를 가져옴
    suspend fun getAllMappings(): List<ParagraphListMapping> {
        return withContext(Dispatchers.IO) {
            mapDao.getAllMappings()
        }
    }
}
