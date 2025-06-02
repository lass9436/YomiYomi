package com.lass.yomiyomi.viewmodel.myWord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.domain.model.WordItem
import com.lass.yomiyomi.data.repository.MyWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyWordViewModel @Inject constructor(
    private val repository: MyWordRepository
) : ViewModel(), MyWordViewModelInterface {

    private val _myWords = MutableStateFlow<List<MyWordItem>>(emptyList())
    override val myWords: StateFlow<List<MyWordItem>> = _myWords

    private val _searchResults = MutableStateFlow<List<WordItem>>(emptyList())
    override val searchResults: StateFlow<List<WordItem>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel

    init {
        loadMyWords()
    }

    // 내 단어 목록 로드
    override fun loadMyWords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myWords.value = if (_selectedLevel.value == Level.ALL) {
                    repository.getAllMyWords()
                } else {
                    repository.getAllMyWordsByLevel(_selectedLevel.value.value!!)
                }
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 원본 단어 검색
    override fun searchOriginalWords(query: String) {
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
    override fun addWordToMyWords(word: WordItem, onResult: (Boolean) -> Unit) {
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

    // 직접 입력으로 내 단어 추가
    override fun addMyWordDirectly(
        word: String,
        reading: String,
        meaning: String,
        type: String,
        level: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (word.isBlank() || reading.isBlank() || meaning.isBlank()) {
            onResult(false, "모든 필드를 입력해주세요.")
            return
        }

        viewModelScope.launch {
            try {
                // 새로운 ID 생성 (타임스탬프 기반)
                val newId = System.currentTimeMillis().toInt()
                
                val myWord = MyWordItem(
                    id = newId,
                    word = word.trim(),
                    reading = reading.trim(),
                    type = type.trim().ifBlank { "명사" },
                    meaning = meaning.trim(),
                    level = level,
                    learningWeight = 0.5f, // 기본 가중치
                    timestamp = System.currentTimeMillis()
                )
                
                repository.insertMyWordDirectly(myWord)
                loadMyWords() // 목록 새로고침
                onResult(true, "단어가 추가되었습니다.")
            } catch (e: Exception) {
                onResult(false, "단어 추가에 실패했습니다: ${e.message}")
            }
        }
    }

    // 내 단어 수정
    override fun updateMyWord(
        myWord: MyWordItem,
        newWord: String,
        newReading: String,
        newMeaning: String,
        newType: String,
        newLevel: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (newWord.isBlank() || newReading.isBlank() || newMeaning.isBlank()) {
            onResult(false, "모든 필드를 입력해주세요.")
            return
        }

        viewModelScope.launch {
            try {
                val updatedMyWord = myWord.copy(
                    word = newWord.trim(),
                    reading = newReading.trim(),
                    type = newType.trim().ifBlank { "명사" },
                    meaning = newMeaning.trim(),
                    level = newLevel,
                    timestamp = System.currentTimeMillis()
                )
                
                repository.insertMyWordDirectly(updatedMyWord) // REPLACE 전략으로 업데이트
                loadMyWords() // 목록 새로고침
                onResult(true, "단어가 수정되었습니다.")
            } catch (e: Exception) {
                onResult(false, "단어 수정에 실패했습니다: ${e.message}")
            }
        }
    }

    // 내 단어 삭제
    override fun deleteMyWord(myWord: MyWordItem) {
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
    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
        loadMyWords()
    }

    // 내 단어 검색
    override fun searchMyWords(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myWords.value = if (query.isBlank()) {
                    if (_selectedLevel.value == Level.ALL) {
                        repository.getAllMyWords()
                    } else {
                        repository.getAllMyWordsByLevel(_selectedLevel.value.value!!)
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
