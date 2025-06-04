package com.lass.yomiyomi.viewmodel.myWord.list

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.entity.WordItem
import kotlinx.coroutines.flow.StateFlow

interface MyWordViewModelInterface {
    val myWords: StateFlow<List<MyWordItem>>
    val searchResults: StateFlow<List<WordItem>>
    val isLoading: StateFlow<Boolean>
    val searchQuery: StateFlow<String>
    val selectedLevel: StateFlow<Level>

    fun loadMyWords()
    fun searchOriginalWords(query: String)
    fun addWordToMyWords(word: WordItem, onResult: (Boolean) -> Unit)
    fun addMyWordDirectly(
        word: String,
        reading: String,
        meaning: String,
        type: String,
        level: String,
        onResult: (Boolean, String) -> Unit
    )
    fun updateMyWord(
        myWord: MyWordItem,
        newWord: String,
        newReading: String,
        newMeaning: String,
        newType: String,
        newLevel: String,
        onResult: (Boolean, String) -> Unit
    )
    fun deleteMyWord(myWord: MyWordItem)
    fun setSelectedLevel(level: Level)
    fun searchMyWords(query: String)
} 
