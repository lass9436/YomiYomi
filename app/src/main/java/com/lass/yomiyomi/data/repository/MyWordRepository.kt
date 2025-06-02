package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.domain.model.WordItem
import com.lass.yomiyomi.domain.model.toMyWordItem
import com.lass.yomiyomi.domain.model.toMyWordItems
import com.lass.yomiyomi.domain.model.toWordItem
import com.lass.yomiyomi.domain.model.toWordItems
import com.lass.yomiyomi.domain.model.toMyWord

class MyWordRepository(private val context: Context) {
    private val myWordDao = AppDatabase.getInstance(context).myWordDao()
    private val wordDao = AppDatabase.getInstance(context).wordDao()

    // 원본 단어를 내 단어로 복제
    suspend fun addWordToMyWords(originalWord: WordItem): Boolean {
        return try {
            val myWord = MyWord(
                id = originalWord.id,
                word = originalWord.word,
                reading = originalWord.reading,
                type = originalWord.type,
                meaning = originalWord.meaning,
                level = originalWord.level,
                learningWeight = originalWord.learningWeight,
                timestamp = System.currentTimeMillis()
            )
            myWordDao.insertMyWord(myWord)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 직접 입력으로 내 단어 추가 (신규 추가 또는 수정)
    suspend fun insertMyWordDirectly(myWordItem: MyWordItem): Boolean {
        return try {
            myWordDao.insertMyWord(myWordItem.toMyWord())
            true
        } catch (e: Exception) {
            false
        }
    }

    // 내 단어 전체 조회
    suspend fun getAllMyWords(): List<MyWordItem> = myWordDao.getAllMyWords().toMyWordItems()

    // 레벨별 내 단어 조회
    suspend fun getAllMyWordsByLevel(level: String): List<MyWordItem> = myWordDao.getAllMyWordsByLevel(level).toMyWordItems()

    // 내 단어 검색
    suspend fun searchMyWords(query: String): List<MyWordItem> = myWordDao.searchMyWords(query).toMyWordItems()

    // 원본 단어 검색 (내 단어에 추가하기 위해)
    suspend fun searchOriginalWords(query: String): List<WordItem> {
        val allWords = wordDao.getAllWords()
        return allWords.filter { word ->
            word.word.contains(query, ignoreCase = true) || 
            word.meaning.contains(query, ignoreCase = true) ||
            word.reading.contains(query, ignoreCase = true)
        }.toWordItems()
    }

    // 내 단어 삭제
    suspend fun deleteMyWord(myWordItem: MyWordItem) {
        myWordDao.deleteMyWord(myWordItem.toMyWord())
    }

    // ID로 내 단어 삭제
    suspend fun deleteMyWordById(id: Int) {
        myWordDao.deleteMyWordById(id)
    }

    // 랜덤 내 단어 조회
    suspend fun getRandomMyWord(): MyWordItem? = myWordDao.getRandomMyWord()?.toMyWordItem()

    // 레벨별 랜덤 내 단어 조회
    suspend fun getRandomMyWordByLevel(level: String?): MyWordItem? = myWordDao.getRandomMyWordByLevel(level)?.toMyWordItem()

    // 학습 모드용 데이터 조회
    suspend fun getMyWordsForLearningMode(level: String): Pair<List<MyWordItem>, List<MyWordItem>> {
        val (topPriority, distractors) = Pair(
            myWordDao.getTopPriorityMyWords(level),
            myWordDao.getRandomMyWordDistractors(level)
        )
        return Pair(
            topPriority.toMyWordItems(),
            distractors.toMyWordItems()
        )
    }

    // 내 단어 학습 상태 업데이트
    suspend fun updateMyWordLearningStatus(wordId: Int, isCorrect: Boolean, currentWeight: Float) {
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
        
        myWordDao.updateMyWordLearningStatus(wordId, clampedWeight, currentTimestamp)
    }

    // 이미 내 단어에 추가되었는지 확인
    suspend fun isWordInMyWords(wordId: Int): Boolean {
        return myWordDao.getMyWordById(wordId) != null
    }
} 