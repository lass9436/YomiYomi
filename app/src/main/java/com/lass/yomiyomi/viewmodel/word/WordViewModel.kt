package com.lass.yomiyomi.viewmodel.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.WordItem
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel(), WordViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _allWords = MutableStateFlow<List<WordItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    override val words: StateFlow<List<WordItem>> = combine(
        _allWords,
        _selectedLevel,
        _searchQuery
    ) { allWords, level, query ->
        var filteredWords = if (level == Level.ALL) {
            allWords
        } else {
            allWords.filter { it.level == level.value }
        }

        if (query.isNotBlank()) {
            filteredWords = filteredWords.filter { word ->
                word.word.contains(query, ignoreCase = true) ||
                word.reading.contains(query, ignoreCase = true) ||
                word.meaning.contains(query, ignoreCase = true)
            }
        }

        filteredWords
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val wordList = wordRepository.getAllWords()
                _allWords.value = wordList
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

    override fun searchWords(query: String) {
        _searchQuery.value = query
    }
} 
