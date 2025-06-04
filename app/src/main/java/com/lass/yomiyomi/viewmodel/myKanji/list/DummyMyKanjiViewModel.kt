package com.lass.yomiyomi.viewmodel.myKanji.list

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DummyMyKanjiViewModel : MyKanjiViewModelInterface {

    private val sampleMyKanji = listOf(
        MyKanjiItem(
            id = 1,
            kanji = "食",
            onyomi = "しょく",
            kunyomi = "た(べる)",
            meaning = "음식, 먹다",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 2,
            kanji = "学",
            onyomi = "がく",
            kunyomi = "まな(ぶ)",
            meaning = "배우다, 학문",
            level = "N4",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 3,
            kanji = "心",
            onyomi = "しん",
            kunyomi = "こころ",
            meaning = "마음, 심장",
            level = "N3",
            learningWeight = 0.9f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 4,
            kanji = "美",
            onyomi = "び",
            kunyomi = "うつく(しい)",
            meaning = "아름다움",
            level = "N2",
            learningWeight = 0.7f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 5,
            kanji = "概",
            onyomi = "がい",
            kunyomi = "",
            meaning = "대략, 개요",
            level = "N1",
            learningWeight = 0.5f,
            timestamp = System.currentTimeMillis()
        )
    )

    private val sampleSearchResults = listOf(
        KanjiItem(
            id = 100,
            kanji = "水",
            onyomi = "すい",
            kunyomi = "みず",
            meaning = "물",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 101,
            kanji = "火",
            onyomi = "か",
            kunyomi = "ひ",
            meaning = "불",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    )

    private val _myKanji = MutableStateFlow(sampleMyKanji)
    override val myKanji: StateFlow<List<MyKanjiItem>> = _myKanji.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _searchResults = MutableStateFlow(sampleSearchResults)
    override val searchResults: StateFlow<List<KanjiItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    override val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
        // 더미에서는 실제 필터링은 하지 않음
    }

    override fun searchMyKanji(query: String) {
        // 더미에서는 실제 검색은 하지 않음
    }

    override fun searchOriginalKanji(query: String) {
        // 더미에서는 실제 검색은 하지 않음
    }

    override fun addKanjiToMyKanji(kanji: KanjiItem) {
        // 더미에서는 실제 추가는 하지 않음
    }

    override fun addMyKanjiDirectly(kanji: String, onyomi: String, kunyomi: String, meaning: String, level: Level) {
        // 더미에서는 실제 추가는 하지 않음
    }

    override fun updateMyKanji(myKanji: MyKanjiItem) {
        // 더미에서는 실제 업데이트는 하지 않음
    }

    override fun deleteMyKanji(myKanji: MyKanjiItem) {
        // 더미에서는 실제 삭제는 하지 않음
    }
} 
