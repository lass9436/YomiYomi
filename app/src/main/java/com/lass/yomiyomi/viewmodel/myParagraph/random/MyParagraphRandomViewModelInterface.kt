package com.lass.yomiyomi.viewmodel.myParagraph.random

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphRandomViewModelInterface {
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val currentParagraph: StateFlow<ParagraphItem?>
    val currentSentences: StateFlow<List<SentenceItem>>
    val availableLevels: StateFlow<List<Level>>
    
    fun setSelectedLevel(level: Level)
    fun loadRandomParagraph()
    fun updateSentenceLearningProgress(id: Int, progress: Float)
} 