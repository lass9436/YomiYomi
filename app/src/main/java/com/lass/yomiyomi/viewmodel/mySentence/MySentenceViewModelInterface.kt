package com.lass.yomiyomi.viewmodel.mySentence

import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MySentenceViewModelInterface {
    val sentences: StateFlow<List<SentenceItem>>
    val isLoading: StateFlow<Boolean>
    val selectedCategory: StateFlow<String>

    fun setSelectedCategory(category: String)
    fun searchSentences(query: String)
    fun updateLearningProgress(id: Int, progress: Float)
} 
