package com.lass.yomiyomi.viewmodel.myKanji

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyKanjiViewModel @Inject constructor(
    private val myKanjiRepository: MyKanjiRepository
) : ViewModel(), MyKanjiViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Kanji>>(emptyList())
    override val searchResults: StateFlow<List<Kanji>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    override val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _allMyKanji = MutableStateFlow<List<MyKanji>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    override val myKanji: StateFlow<List<MyKanji>> = combine(
        _allMyKanji,
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        loadMyKanji()
    }

    private fun loadMyKanji() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val kanjiList = myKanjiRepository.getAllMyKanji()
                _allMyKanji.value = kanjiList
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

    override fun searchMyKanji(query: String) {
        _searchQuery.value = query
    }

    override fun searchOriginalKanji(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val results = myKanjiRepository.searchOriginalKanji(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    override fun addKanjiToMyKanji(kanji: Kanji) {
        viewModelScope.launch {
            try {
                myKanjiRepository.addKanjiToMyKanji(kanji)
                loadMyKanji() // 목록 새로고침
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun addMyKanjiDirectly(kanji: String, onyomi: String, kunyomi: String, meaning: String, level: Level) {
        viewModelScope.launch {
            try {
                val newId = System.currentTimeMillis().toInt() // 임시 ID 생성
                val myKanji = MyKanji(
                    id = newId,
                    kanji = kanji,
                    onyomi = onyomi,
                    kunyomi = kunyomi,
                    meaning = meaning,
                    level = level.value ?: "N5",
                    learningWeight = 1.0f,
                    timestamp = System.currentTimeMillis()
                )
                myKanjiRepository.insertMyKanjiDirectly(myKanji)
                loadMyKanji() // 목록 새로고침
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun updateMyKanji(myKanji: MyKanji) {
        viewModelScope.launch {
            try {
                myKanjiRepository.insertMyKanjiDirectly(myKanji) // REPLACE 전략으로 업데이트
                loadMyKanji() // 목록 새로고침
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun deleteMyKanji(myKanji: MyKanji) {
        viewModelScope.launch {
            try {
                myKanjiRepository.deleteMyKanji(myKanji)
                loadMyKanji() // 목록 새로고침
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 