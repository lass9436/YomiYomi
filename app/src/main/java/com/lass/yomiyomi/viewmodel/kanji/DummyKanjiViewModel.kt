package com.lass.yomiyomi.viewmodel.kanji

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DummyKanjiViewModel : KanjiViewModelInterface {

    private val sampleKanji = listOf(
        KanjiItem(
            id = 1,
            kanji = "食",
            onyomi = "しょく",
            kunyomi = "た(べる)",
            meaning = "음식, 먹다",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 2,
            kanji = "学",
            onyomi = "がく",
            kunyomi = "まな(ぶ)",
            meaning = "배우다, 학문",
            level = "N4",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 3,
            kanji = "心",
            onyomi = "しん",
            kunyomi = "こころ",
            meaning = "마음, 심장",
            level = "N3",
            learningWeight = 0.9f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 4,
            kanji = "美",
            onyomi = "び",
            kunyomi = "うつく(しい)",
            meaning = "아름다움",
            level = "N2",
            learningWeight = 0.7f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
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

    private val _kanji = MutableStateFlow(sampleKanji)
    override val kanji: StateFlow<List<KanjiItem>> = _kanji.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
        // Filter dummy data based on level
        if (level == Level.ALL) {
            _kanji.value = sampleKanji
        } else {
            _kanji.value = sampleKanji.filter { it.level == level.value }
        }
    }

    override fun searchKanji(query: String) {
        if (query.isBlank()) {
            _kanji.value = sampleKanji
        } else {
            _kanji.value = sampleKanji.filter { kanji ->
                kanji.kanji.contains(query, ignoreCase = true) ||
                kanji.onyomi.contains(query, ignoreCase = true) ||
                kanji.kunyomi.contains(query, ignoreCase = true) ||
                kanji.meaning.contains(query, ignoreCase = true)
            }
        }
    }
} 
