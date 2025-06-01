package com.lass.yomiyomi.viewmodel.myWord

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.data.model.Word
import kotlinx.coroutines.flow.StateFlow

interface MyWordViewModelInterface {
    val myWords: StateFlow<List<MyWord>>
    val searchResults: StateFlow<List<Word>>
    val isLoading: StateFlow<Boolean>
    val searchQuery: StateFlow<String>
    val selectedLevel: StateFlow<Level>

    fun loadMyWords()
    fun searchOriginalWords(query: String)
    fun addWordToMyWords(word: Word, onResult: (Boolean) -> Unit)
    fun addMyWordDirectly(
        word: String,
        reading: String,
        meaning: String,
        type: String,
        level: String,
        onResult: (Boolean, String) -> Unit
    )
    fun updateMyWord(
        myWord: MyWord,
        newWord: String,
        newReading: String,
        newMeaning: String,
        newType: String,
        newLevel: String,
        onResult: (Boolean, String) -> Unit
    )
    fun deleteMyWord(myWord: MyWord)
    fun setSelectedLevel(level: Level)
    fun searchMyWords(query: String)
} 