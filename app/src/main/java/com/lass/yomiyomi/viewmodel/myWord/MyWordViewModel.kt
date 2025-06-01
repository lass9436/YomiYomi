package com.lass.yomiyomi.viewmodel.myWord

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.repository.MyWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyWordViewModel(context: Context) : ViewModel() {
    private val repository = MyWordRepository(context)

    private val _myWords = MutableStateFlow<List<MyWord>>(emptyList())
    val myWords: StateFlow<List<MyWord>> = _myWords

    private val _searchResults = MutableStateFlow<List<Word>>(emptyList())
    val searchResults: StateFlow<List<Word>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLevel = MutableStateFlow("ALL")
    val selectedLevel: StateFlow<String> = _selectedLevel

    init {
        loadMyWords()
    }

    // 내 단어 목록 로드
    fun loadMyWords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myWords.value = if (_selectedLevel.value == "ALL") {
                    repository.getAllMyWords()
                } else {
                    repository.getAllMyWordsByLevel(_selectedLevel.value)
                }
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 원본 단어 검색
    fun searchOriginalWords(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = repository.searchOriginalWords(query)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 단어를 내 단어에 추가
    fun addWordToMyWords(word: Word, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val isAlreadyAdded = repository.isWordInMyWords(word.id)
                if (isAlreadyAdded) {
                    onResult(false) // 이미 추가된 단어
                    return@launch
                }
                
                val success = repository.addWordToMyWords(word)
                if (success) {
                    loadMyWords() // 목록 새로고침
                }
                onResult(success)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // 내 단어 삭제
    fun deleteMyWord(myWord: MyWord) {
        viewModelScope.launch {
            try {
                repository.deleteMyWord(myWord)
                loadMyWords() // 목록 새로고침
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    // 레벨 필터 변경
    fun setSelectedLevel(level: String) {
        _selectedLevel.value = level
        loadMyWords()
    }

    // 내 단어 검색
    fun searchMyWords(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myWords.value = if (query.isBlank()) {
                    if (_selectedLevel.value == "ALL") {
                        repository.getAllMyWords()
                    } else {
                        repository.getAllMyWordsByLevel(_selectedLevel.value)
                    }
                } else {
                    repository.searchMyWords(query)
                }
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }
} 