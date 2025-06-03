package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.database.WordDataImporter
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.mapper.toWordItem
import com.lass.yomiyomi.domain.model.mapper.toWordItems

class WordRepository(private val context: Context) {
    private val wordDao = AppDatabase.getInstance(context).wordDao() // WordDao 초기화

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun importWordData(context: Context) {
        val wordList = WordDataImporter.importWordsFromCsv(context) // CSV 데이터 가져오기
        wordDao.insertAll(wordList) // Room DB에 데이터 추가
    }

    suspend fun getAllWords(): List<WordItem> = wordDao.getAllWords().toWordItems() // 전체 Word 조회

    suspend fun getAllWordsByLevel(level: String): List<WordItem> = wordDao.getAllWordsByLevel(level).toWordItems() // 특정 Level 조회

    suspend fun getRandomWord(): WordItem? = wordDao.getRandomWord()?.toWordItem() // 랜덤 Word 하나 조회

    suspend fun getRandomWordByLevel(level: String?): WordItem? = wordDao.getRandomWordByLevel(level)?.toWordItem() // 특정 Level에서 랜덤 Word 조회

    // 학습 모드용 데이터 조회
    suspend fun getWordsForLearningMode(level: String): Pair<List<WordItem>, List<WordItem>> {
        val (topPriority, distractors) = Pair(
            wordDao.getTopPriorityWords(level),
            wordDao.getRandomDistractors(level)
        )
        return Pair(
            topPriority.toWordItems(),
            distractors.toWordItems()
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
