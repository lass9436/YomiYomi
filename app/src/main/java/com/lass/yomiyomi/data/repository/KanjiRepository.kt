package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.KanjiDataImporter
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.Kanji

class KanjiRepository(private val context: Context) {
    private val kanjiDao = AppDatabase.getInstance(context).kanjiDao()

    /**
     * CSV 파일을 불러와 Room 데이터베이스로 삽입
     */
    suspend fun initializeDatabase() {
        val kanjiList = KanjiDataImporter.importKanjiFromCsv(context)
        kanjiDao.insertAll(kanjiList)
    }

    suspend fun getAllKanji() = kanjiDao.getAllKanji()

    suspend fun getAllKanjiByLevel(level: String): List<Kanji> = kanjiDao.getAllKanjiByLevel(level)

    suspend fun getRandomKanji() = kanjiDao.getRandomKanji()

    suspend fun getRandomKanjiByLevel(level: String?): Kanji? = kanjiDao.getRandomKanjiByLevel(level)

    // 학습 모드용 데이터 조회
    suspend fun getKanjiForLearningMode(level: String): Pair<List<Kanji>, List<Kanji>> {
        return Pair(
            kanjiDao.getTopPriorityKanji(level),
            kanjiDao.getRandomDistractors(level)
        )
    }

    // 가중치 업데이트
    suspend fun updateKanjiLearningStatus(kanjiId: Int, isCorrect: Boolean, currentWeight: Float) {
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
        
        kanjiDao.updateKanjiLearningStatus(kanjiId, clampedWeight, currentTimestamp)
    }
}