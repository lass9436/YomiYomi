package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.database.WordDataImporter
import com.lass.yomiyomi.data.model.Word


class WordRepository(private val context: Context) {
    private val wordDao = AppDatabase.getInstance(context).wordDao() // WordDao 초기화

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun initializeDatabase() {
        val wordList = WordDataImporter.importWordsFromCsv(context) // CSV 데이터 가져오기
        wordDao.insertAll(wordList) // Room DB에 데이터 추가
    }

    suspend fun getAllWords() = wordDao.getAllWords() // 전체 Word 조회

    suspend fun getAllWordsByLevel(level: String): List<Word> = wordDao.getAllWordsByLevel(level) // 특정 Level 조회

    suspend fun getRandomWord() = wordDao.getRandomWord() // 랜덤 Word 하나 조회

    suspend fun getRandomWordByLevel(level: String?): Word? = wordDao.getRandomWordByLevel(level) // 특정 Level에서 랜덤 Word 조회
}
