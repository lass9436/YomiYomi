package com.lass.yomiyomi.viewmodel.mySentence.random

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MySentenceRandomViewModelInterface {
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val currentSentence: StateFlow<SentenceItem?>
    val availableLevels: StateFlow<List<Level>>
    
    fun setSelectedLevel(level: Level)
    fun loadRandomSentence()
    fun updateLearningProgress(id: Int, progress: Float)
} 