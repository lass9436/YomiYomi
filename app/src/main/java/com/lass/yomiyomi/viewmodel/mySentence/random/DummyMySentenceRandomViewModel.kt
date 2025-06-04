package com.lass.yomiyomi.viewmodel.mySentence.random

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMySentenceRandomViewModel : MySentenceRandomViewModelInterface {
    
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel
    
    private val _currentSentence = MutableStateFlow<SentenceItem?>(null)
    override val currentSentence: StateFlow<SentenceItem?> = _currentSentence
    
    private val _availableLevels = MutableStateFlow(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels
    
    private val dummySentence = SentenceItem(
        id = 1,
        japanese = "これは日本語です。",
        korean = "이것은 일본어입니다.",
        paragraphId = "dummy_1",
        orderInParagraph = 1,
        category = "일반",
        level = Level.N5,
        learningProgress = 0.0f,
        reviewCount = 0,
        lastReviewedAt = null,
        createdAt = System.currentTimeMillis()
    )
    
    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }
    
    override fun loadRandomSentence() {
        _currentSentence.value = dummySentence
    }
    
    override fun updateLearningProgress(id: Int, progress: Float) {
        // 더미 구현에서는 아무 동작도 하지 않음
    }
} 