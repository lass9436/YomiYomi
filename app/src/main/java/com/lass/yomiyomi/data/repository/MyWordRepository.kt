package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.data.model.Word

class MyWordRepository(private val context: Context) {
    private val myWordDao = AppDatabase.getInstance(context).myWordDao()
    private val wordDao = AppDatabase.getInstance(context).wordDao()

    // 원본 단어를 내 단어로 복제
    suspend fun addWordToMyWords(originalWord: Word): Boolean {
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
    suspend fun insertMyWordDirectly(myWord: MyWord): Boolean {
        return try {
            myWordDao.insertMyWord(myWord)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 내 단어 전체 조회
    suspend fun getAllMyWords(): List<MyWord> = myWordDao.getAllMyWords()

    // 레벨별 내 단어 조회
    suspend fun getAllMyWordsByLevel(level: String): List<MyWord> = myWordDao.getAllMyWordsByLevel(level)

    // 내 단어 검색
    suspend fun searchMyWords(query: String): List<MyWord> = myWordDao.searchMyWords(query)

    // 원본 단어 검색 (내 단어에 추가하기 위해)
    suspend fun searchOriginalWords(query: String): List<Word> {
        val allWords = wordDao.getAllWords()
        return allWords.filter { word ->
            word.word.contains(query, ignoreCase = true) || 
            word.meaning.contains(query, ignoreCase = true) ||
            word.reading.contains(query, ignoreCase = true)
        }
    }

    // 내 단어 삭제
    suspend fun deleteMyWord(myWord: MyWord) {
        myWordDao.deleteMyWord(myWord)
    }

    // ID로 내 단어 삭제
    suspend fun deleteMyWordById(id: Int) {
        myWordDao.deleteMyWordById(id)
    }

    // 랜덤 내 단어 조회
    suspend fun getRandomMyWord(): MyWord? = myWordDao.getRandomMyWord()

    // 레벨별 랜덤 내 단어 조회
    suspend fun getRandomMyWordByLevel(level: String?): MyWord? = myWordDao.getRandomMyWordByLevel(level)

    // 학습 모드용 데이터 조회
    suspend fun getMyWordsForLearningMode(level: String): Pair<List<MyWord>, List<MyWord>> {
        return Pair(
            myWordDao.getTopPriorityMyWords(level),
            myWordDao.getRandomMyWordDistractors(level)
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