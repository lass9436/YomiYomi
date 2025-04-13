package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.KanjiDataImporter
import com.lass.yomiyomi.data.db.AppDatabase // AppDatabase로 수정

class KanjiRepository(private val context: Context) {
    private val kanjiDao = AppDatabase.getInstance(context).kanjiDao() // AppDatabase 사용

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun initializeDatabase() {
        val kanjiList = KanjiDataImporter.importKanjiFromCsv(context)
        kanjiDao.insertAll(kanjiList) // Room DB에 데이터 추가
    }

    suspend fun getAllKanji() = kanjiDao.getAllKanji() // 전체 Kanji 조회
}