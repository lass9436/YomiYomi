package com.lass.yomiyomi.viewmodel.mySentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.SentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceViewModel @Inject constructor(
    private val sentenceRepository: SentenceRepository
) : ViewModel(), MySentenceViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    override val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _allSentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

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
                val sentenceList = sentenceRepository.getAllSentences()
                _allSentences.value = sentenceList
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
                sentenceRepository.updateLearningProgress(id, progress)
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
                sentenceRepository.insertSentence(sentence)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateSentence(sentence: SentenceItem) {
        viewModelScope.launch {
            try {
                sentenceRepository.updateSentence(sentence)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteSentence(sentenceId: Int) {
        viewModelScope.launch {
            try {
                sentenceRepository.deleteSentenceById(sentenceId)
                loadSentences()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 
