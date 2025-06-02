package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.domain.model.KanjiItem
import com.lass.yomiyomi.domain.model.toMyKanjiItem
import com.lass.yomiyomi.domain.model.toMyKanjiItems
import com.lass.yomiyomi.domain.model.toKanjiItem
import com.lass.yomiyomi.domain.model.toKanjiItems
import com.lass.yomiyomi.domain.model.toMyKanji

class MyKanjiRepository(private val context: Context) {
    private val myKanjiDao = AppDatabase.getInstance(context).myKanjiDao()
    private val kanjiDao = AppDatabase.getInstance(context).kanjiDao()

    // 원본 한자를 내 한자로 복제
    suspend fun addKanjiToMyKanji(originalKanji: KanjiItem): Boolean {
        return try {
            val myKanji = MyKanji(
                id = originalKanji.id,
                kanji = originalKanji.kanji,
                onyomi = originalKanji.onyomi,
                kunyomi = originalKanji.kunyomi,
                meaning = originalKanji.meaning,
                level = originalKanji.level,
                learningWeight = originalKanji.learningWeight,
                timestamp = System.currentTimeMillis()
            )
            myKanjiDao.insertMyKanji(myKanji)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 직접 입력으로 내 한자 추가 (신규 추가 또는 수정)
    suspend fun insertMyKanjiDirectly(myKanjiItem: MyKanjiItem): Boolean {
        return try {
            myKanjiDao.insertMyKanji(myKanjiItem.toMyKanji())
            true
        } catch (e: Exception) {
            false
        }
    }

    // 내 한자 전체 조회
    suspend fun getAllMyKanji(): List<MyKanjiItem> = myKanjiDao.getAllMyKanji().toMyKanjiItems()

    // 레벨별 내 한자 조회
    suspend fun getAllMyKanjiByLevel(level: String): List<MyKanjiItem> = myKanjiDao.getMyKanjiByLevel(level).toMyKanjiItems()

    // 내 한자 검색
    suspend fun searchMyKanji(query: String): List<MyKanjiItem> = myKanjiDao.searchMyKanji(query).toMyKanjiItems()

    // 원본 한자 검색 (내 한자에 추가하기 위해)
    suspend fun searchOriginalKanji(query: String): List<KanjiItem> {
        val allKanji = kanjiDao.getAllKanji()
        return allKanji.filter { kanji ->
            kanji.kanji.contains(query, ignoreCase = true) || 
            kanji.meaning.contains(query, ignoreCase = true) ||
            kanji.onyomi.contains(query, ignoreCase = true) ||
            kanji.kunyomi.contains(query, ignoreCase = true)
        }.toKanjiItems()
    }

    // 내 한자 삭제
    suspend fun deleteMyKanji(myKanjiItem: MyKanjiItem) {
        myKanjiDao.deleteMyKanji(myKanjiItem.toMyKanji())
    }

    // ID로 내 한자 삭제
    suspend fun deleteMyKanjiById(id: Int) {
        myKanjiDao.deleteMyKanjiById(id)
    }

    // 이미 내 한자에 추가되었는지 확인
    suspend fun isKanjiInMyKanji(kanjiId: Int): Boolean {
        // MyKanjiDao에 이 메서드가 없으므로 나중에 추가 필요
        return false // 임시
    }

    // 랜덤 내 한자 조회
    suspend fun getRandomMyKanji(): MyKanjiItem? {
        val allMyKanji = myKanjiDao.getAllMyKanji()
        return allMyKanji.randomOrNull()?.toMyKanjiItem()
    }

    // 레벨별 랜덤 내 한자 조회
    suspend fun getRandomMyKanjiByLevel(level: String?): MyKanjiItem? {
        return if (level == null || level == "ALL") {
            getRandomMyKanji()
        } else {
            val kanjiByLevel = myKanjiDao.getMyKanjiByLevel(level)
            kanjiByLevel.randomOrNull()?.toMyKanjiItem()
        }
    }

    // 학습 모드용 데이터 조회
    suspend fun getMyKanjiForLearningMode(level: String): Pair<List<MyKanjiItem>, List<MyKanjiItem>> {
        val (topPriority, distractors) = Pair(
            myKanjiDao.getTopPriorityMyKanji(level),
            myKanjiDao.getRandomMyKanjiDistractors(level)
        )
        return Pair(
            topPriority.toMyKanjiItems(),
            distractors.toMyKanjiItems()
        )
    }

    // 내 한자 학습 상태 업데이트
    suspend fun updateMyKanjiLearningStatus(kanjiId: Int, isCorrect: Boolean, currentWeight: Float) {
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
        
        myKanjiDao.updateMyKanjiLearningStatus(kanjiId, clampedWeight, currentTimestamp)
    }
} 