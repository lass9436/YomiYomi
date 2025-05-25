package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.KanjiDataImporter
import com.lass.yomiyomi.data.database.AppDatabase // AppDatabase로 수정
import com.lass.yomiyomi.data.model.Kanji

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

    suspend fun getAllKanjiByLevel(level: String?): List<Kanji> = kanjiDao.getAllKanjiByLevel(level)

    suspend fun getRandomKanji() = kanjiDao.getRandomKanji()

    suspend fun getRandomKanjiByLevel(level: String?): List<Kanji> = kanjiDao.getRandomKanjiByLevel(level)
}