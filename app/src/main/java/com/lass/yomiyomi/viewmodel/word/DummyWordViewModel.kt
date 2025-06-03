package com.lass.yomiyomi.viewmodel.word

import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.constant.Level
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DummyWordViewModel : WordViewModelInterface {

    private val sampleWords = listOf(
        WordItem(
            id = 1,
            word = "食べる",
            reading = "たべる",
            meaning = "먹다",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis(),
            type = "동사"
        ),
        WordItem(
            id = 2,
            word = "勉強",
            reading = "べんきょう",
            meaning = "공부",
            level = "N4",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis(),
            type = "명사"
        ),
        WordItem(
            id = 3,
            word = "心配",
            reading = "しんぱい",
            meaning = "걱정",
            level = "N3",
            learningWeight = 0.9f,
            timestamp = System.currentTimeMillis(),
            type = "명사"
        ),
        WordItem(
            id = 4,
            word = "美しい",
            reading = "うつくしい",
            meaning = "아름답다",
            level = "N2",
            learningWeight = 0.7f,
            timestamp = System.currentTimeMillis(),
            type = "형용사"
        ),
        WordItem(
            id = 5,
            word = "概念",
            reading = "がいねん",
            meaning = "개념",
            level = "N1",
            learningWeight = 0.5f,
            timestamp = System.currentTimeMillis(),
            type = "명사"
        )
    )

    private val _words = MutableStateFlow(sampleWords)
    override val words: StateFlow<List<WordItem>> = _words.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
        // Filter dummy data based on level
        if (level == Level.ALL) {
            _words.value = sampleWords
        } else {
            _words.value = sampleWords.filter { it.level == level.value }
        }
    }

    override fun searchWords(query: String) {
        if (query.isBlank()) {
            _words.value = sampleWords
        } else {
            _words.value = sampleWords.filter { word ->
                word.word.contains(query, ignoreCase = true) ||
                word.reading.contains(query, ignoreCase = true) ||
                word.meaning.contains(query, ignoreCase = true)
            }
        }
    }
} 
