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
    suspend fun importWordData(context: Context) {
        val wordList = WordDataImporter.importWordsFromCsv(context) // CSV 데이터 가져오기
        wordDao.insertAll(wordList) // Room DB에 데이터 추가
    }

    suspend fun getAllWords() = wordDao.getAllWords() // 전체 Word 조회

    suspend fun getAllWordsByLevel(level: String): List<Word> = wordDao.getAllWordsByLevel(level) // 특정 Level 조회

    suspend fun getRandomWord() = wordDao.getRandomWord() // 랜덤 Word 하나 조회

    suspend fun getRandomWordByLevel(level: String?): Word? = wordDao.getRandomWordByLevel(level) // 특정 Level에서 랜덤 Word 조회

    // 학습 모드용 데이터 조회
    suspend fun getWordsForLearningMode(level: String): Pair<List<Word>, List<Word>> {
        return Pair(
            wordDao.getTopPriorityWords(level),
            wordDao.getRandomDistractors(level)
        )
    }

    // 가중치 업데이트
    suspend fun updateWordLearningStatus(wordId: Int, isCorrect: Boolean, currentWeight: Float) {
        val newWeight = if (isCorrect) {
            // 정답: w_new = w_old - (w_old * w_old) * 0.4
            currentWeight - (currentWeight * currentWeight * 0.4f)
        } else {
            // 오답: w_new = w_old + ((1-w_old) * (1-w_old)) * 0.4
            val inverseWeight = 1 - currentWeight
            currentWeight + (inverseWeight * inverseWeight * 0.4f)
        }
        
        val clampedWeight = newWeight.coerceIn(0f, 1f)
        val currentTimestamp = System.currentTimeMillis()
        
        wordDao.updateWordLearningStatus(wordId, clampedWeight, currentTimestamp)
    }
}
