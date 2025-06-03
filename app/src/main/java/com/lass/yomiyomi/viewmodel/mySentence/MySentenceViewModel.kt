package com.lass.yomiyomi.viewmodel.mySentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.MySentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceViewModel @Inject constructor(
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MySentenceViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    override val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _allSentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    // 동적 카테고리와 난이도 목록
    private val _availableCategories = MutableStateFlow<List<String>>(emptyList())
    val availableCategories: StateFlow<List<String>> = _availableCategories.asStateFlow()

    private val _availableDifficulties = MutableStateFlow<List<String>>(emptyList())
    val availableDifficulties: StateFlow<List<String>> = _availableDifficulties.asStateFlow()

    override val sentences: StateFlow<List<SentenceItem>> = combine(
        _allSentences,
        _selectedCategory,
        _searchQuery
    ) { allSentences, category, query ->
        var filteredSentences = if (category == "ALL") {
            allSentences
        } else {
            allSentences.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            filteredSentences = filteredSentences.filter { sentence ->
                sentence.japanese.contains(query, ignoreCase = true) ||
                sentence.korean.contains(query, ignoreCase = true) ||
                sentence.category.contains(query, ignoreCase = true)
            }
        }

        filteredSentences
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadSentences()
    }

    private fun loadSentences() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 문단에 속하지 않은 독립 문장들만 가져오기
                val sentenceList = mySentenceRepository.getIndividualSentences()
                _allSentences.value = sentenceList
                
                // 카테고리와 난이도 목록도 함께 업데이트
                val categories = mySentenceRepository.getDistinctCategories()
                _availableCategories.value = categories
                
                val difficulties = mySentenceRepository.getDistinctDifficulties()
                _availableDifficulties.value = difficulties
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    override fun searchSentences(query: String) {
        _searchQuery.value = query
    }

    override fun updateLearningProgress(id: Int, progress: Float) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateLearningProgress(id, progress)
                // 진도 업데이트 후 데이터 새로고침
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // CRUD 기능들
    fun insertSentence(sentence: SentenceItem) {
        viewModelScope.launch {
            try {
                mySentenceRepository.insertSentence(sentence)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateSentence(sentence: SentenceItem) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateSentence(sentence)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteSentence(sentenceId: Int) {
        viewModelScope.launch {
            try {
                mySentenceRepository.deleteSentenceById(sentenceId)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 
