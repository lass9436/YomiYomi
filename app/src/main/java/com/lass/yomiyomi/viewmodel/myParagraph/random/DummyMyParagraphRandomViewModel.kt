package com.lass.yomiyomi.viewmodel.myParagraph.random

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMyParagraphRandomViewModel : MyParagraphRandomViewModelInterface {
    
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel
    
    private val _currentParagraph = MutableStateFlow<ParagraphItem?>(null)
    override val currentParagraph: StateFlow<ParagraphItem?> = _currentParagraph
    
    private val _currentSentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    override val currentSentences: StateFlow<List<SentenceItem>> = _currentSentences
    
    private val _availableLevels = MutableStateFlow(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels
    
    private val dummyParagraph = ParagraphItem(
        paragraphId = "dummy_1",
        title = "더미 문단",
        description = "프리뷰용 더미 문단입니다",
        category = "일반",
        level = Level.N5,
        totalSentences = 2,
        actualSentenceCount = 2,
        createdAt = System.currentTimeMillis()
    )
    
    private val dummySentences = listOf(
        SentenceItem(
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
        ),
        SentenceItem(
            id = 2,
            japanese = "日本語を勉強しています。",
            korean = "일본어를 공부하고 있습니다.",
            paragraphId = "dummy_1",
            orderInParagraph = 2,
            category = "일반",
            level = Level.N5,
            learningProgress = 0.0f,
            reviewCount = 0,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )
    )
    
    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }
    
    override fun loadRandomParagraph() {
        _currentParagraph.value = dummyParagraph
        _currentSentences.value = dummySentences
    }
    
    override fun updateSentenceLearningProgress(id: Int, progress: Float) {
        // 더미 구현에서는 아무 동작도 하지 않음
    }
} 