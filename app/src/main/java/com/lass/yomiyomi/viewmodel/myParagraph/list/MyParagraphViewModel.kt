package com.lass.yomiyomi.viewmodel.myParagraph.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.constant.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MyParagraphViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    override val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    private val _allParagraphs = MutableStateFlow<List<ParagraphItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    private val _learningProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val learningProgress: StateFlow<Map<String, Float>> = _learningProgress.asStateFlow()

    private val _sentenceCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val sentenceCounts: StateFlow<Map<String, Int>> = _sentenceCounts.asStateFlow()

    override val paragraphs: StateFlow<List<ParagraphItem>> = combine(
        _allParagraphs,
        _selectedCategory,
        _selectedLevel,
        _searchQuery
    ) { allParagraphs, category, level, query ->
        var filteredParagraphs = if (category == "ALL") {
            allParagraphs
        } else {
            allParagraphs.filter { it.category == category }
        }

        if (level != Level.ALL) {
            filteredParagraphs = filteredParagraphs.filter { it.level == level }
        }

        if (query.isNotBlank()) {
            filteredParagraphs = filteredParagraphs.filter { paragraph ->
                paragraph.title.contains(query, ignoreCase = true) ||
                paragraph.description.contains(query, ignoreCase = true) ||
                paragraph.category.contains(query, ignoreCase = true)
            }
        }

        filteredParagraphs
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadParagraphs()
    }

    private fun loadParagraphs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 문장 개수와 함께 조회
                val paragraphList = myParagraphRepository.getParagraphsWithSentenceCounts()
                _allParagraphs.value = paragraphList
                
                // 🔥 문단별 학습 진도도 함께 로드
                val progressMap = mySentenceRepository.getLearningProgressByParagraph()
                _learningProgress.value = progressMap
                
                // 🔥 문단별 문장 개수도 함께 로드
                val countsMap = mySentenceRepository.getSentenceCountsByParagraph()
                _sentenceCounts.value = countsMap
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

    fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun searchParagraphs(query: String) {
        _searchQuery.value = query
    }

    // CRUD 기능들
    fun insertParagraph(paragraph: ParagraphItem) {
        viewModelScope.launch {
            try {
                myParagraphRepository.insertParagraph(paragraph)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateParagraph(paragraph: ParagraphItem) {
        viewModelScope.launch {
            try {
                myParagraphRepository.updateParagraph(paragraph)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteParagraph(paragraphId: String) {
        viewModelScope.launch {
            try {
                myParagraphRepository.deleteParagraphById(paragraphId)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 🔥 학습 진도 새로고침 메서드 추가
    fun refreshLearningProgress() {
        viewModelScope.launch {
            try {
                val progressMap = mySentenceRepository.getLearningProgressByParagraph()
                _learningProgress.value = progressMap
                
                // 🔥 문장 개수도 함께 새로고침
                val countsMap = mySentenceRepository.getSentenceCountsByParagraph()
                _sentenceCounts.value = countsMap
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 특정 문단 조회
    suspend fun getParagraphById(paragraphId: String): ParagraphItem? {
        return try {
            myParagraphRepository.getParagraphById(paragraphId)
        } catch (e: Exception) {
            null
        }
    }
} 
