package com.lass.yomiyomi.viewmodel.sentence

import com.lass.yomiyomi.domain.model.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface SentenceViewModelInterface {
    val sentences: StateFlow<List<SentenceItem>>
    val isLoading: StateFlow<Boolean>
    val selectedCategory: StateFlow<String>

    fun setSelectedCategory(category: String)
    fun searchSentences(query: String)
    fun updateLearningProgress(id: Int, progress: Float)
} 