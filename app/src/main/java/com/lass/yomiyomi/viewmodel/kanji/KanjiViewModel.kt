package com.lass.yomiyomi.viewmodel.kanji

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KanjiViewModel @Inject constructor(
    private val kanjiRepository: KanjiRepository
) : ViewModel(), KanjiViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _allKanji = MutableStateFlow<List<KanjiItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    override val kanji: StateFlow<List<KanjiItem>> = combine(
        _allKanji,
        _selectedLevel,
        _searchQuery
    ) { allKanji, level, query ->
        var filteredKanji = if (level == Level.ALL) {
            allKanji
        } else {
            allKanji.filter { it.level == level.value }
        }

        if (query.isNotBlank()) {
            filteredKanji = filteredKanji.filter { kanji ->
                kanji.kanji.contains(query, ignoreCase = true) ||
                kanji.onyomi.contains(query, ignoreCase = true) ||
                kanji.kunyomi.contains(query, ignoreCase = true) ||
                kanji.meaning.contains(query, ignoreCase = true)
            }
        }

        filteredKanji
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadKanji()
    }

    private fun loadKanji() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val kanjiList = kanjiRepository.getAllKanji()
                _allKanji.value = kanjiList
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun searchKanji(query: String) {
        _searchQuery.value = query
    }
} 
