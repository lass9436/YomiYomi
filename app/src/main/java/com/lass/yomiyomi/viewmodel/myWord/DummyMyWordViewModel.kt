package com.lass.yomiyomi.viewmodel.myWord

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.entity.WordItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMyWordViewModel : MyWordViewModelInterface {

    private val _myWords = MutableStateFlow(
        listOf(
            MyWordItem(
                id = 1,
                word = "食べる",
                reading = "たべる",
                type = "동사",
                meaning = "먹다",
                level = "N5",
                learningWeight = 0.5f,
                timestamp = System.currentTimeMillis()
            ),
            MyWordItem(
                id = 2,
                word = "勉強",
                reading = "べんきょう",
                type = "명사",
                meaning = "공부",
                level = "N4",
                learningWeight = 0.3f,
                timestamp = System.currentTimeMillis()
            ),
            MyWordItem(
                id = 3,
                word = "桜",
                reading = "さくら",
                type = "명사",
                meaning = "벚꽃",
                level = "N2",
                learningWeight = 0.8f,
                timestamp = System.currentTimeMillis()
            ),
            MyWordItem(
                id = 4,
                word = "概念",
                reading = "がいねん",
                type = "명사",
                meaning = "개념",
                level = "N1",
                learningWeight = 0.9f,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    override val myWords: StateFlow<List<MyWordItem>> = _myWords

    private val _searchResults = MutableStateFlow<List<WordItem>>(emptyList())
    override val searchResults: StateFlow<List<WordItem>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel

    override fun loadMyWords() {
        // Dummy implementation - do nothing
    }

    override fun searchOriginalWords(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _searchResults.value = listOf(
                WordItem(
                    id = 100,
                    word = "愛",
                    reading = "あい",
                    type = "명사",
                    meaning = "사랑",
                    level = "N3",
                    learningWeight = 0.5f,
                    timestamp = System.currentTimeMillis()
                ),
                WordItem(
                    id = 101,
                    word = "美しい",
                    reading = "うつくしい",
                    type = "형용사",
                    meaning = "아름답다",
                    level = "N3",
                    learningWeight = 0.5f,
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            _searchResults.value = emptyList()
        }
    }

    override fun addWordToMyWords(word: WordItem, onResult: (Boolean) -> Unit) {
        onResult(true)
    }

    override fun addMyWordDirectly(
        word: String,
        reading: String,
        meaning: String,
        type: String,
        level: String,
        onResult: (Boolean, String) -> Unit
    ) {
        onResult(true, "단어가 추가되었습니다.")
    }

    override fun updateMyWord(
        myWord: MyWordItem,
        newWord: String,
        newReading: String,
        newMeaning: String,
        newType: String,
        newLevel: String,
        onResult: (Boolean, String) -> Unit
    ) {
        onResult(true, "단어가 수정되었습니다.")
    }

    override fun deleteMyWord(myWord: MyWordItem) {
        // Dummy implementation - do nothing
    }

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun searchMyWords(query: String) {
        // Dummy implementation - do nothing
    }
} 
