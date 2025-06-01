package com.lass.yomiyomi.viewmodel.myKanji

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyKanji
import kotlinx.coroutines.flow.StateFlow

interface MyKanjiViewModelInterface {
    val myKanji: StateFlow<List<MyKanji>>
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val searchResults: StateFlow<List<Kanji>>
    val isSearching: StateFlow<Boolean>

    fun setSelectedLevel(level: Level)
    fun searchMyKanji(query: String)
    fun searchOriginalKanji(query: String)
    fun addKanjiToMyKanji(kanji: Kanji)
    fun addMyKanjiDirectly(kanji: String, onyomi: String, kunyomi: String, meaning: String, level: Level)
    fun updateMyKanji(myKanji: MyKanji)
    fun deleteMyKanji(myKanji: MyKanji)
} 