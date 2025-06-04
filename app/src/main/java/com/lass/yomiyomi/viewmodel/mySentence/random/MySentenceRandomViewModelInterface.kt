package com.lass.yomiyomi.viewmodel.mySentence.random

import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MySentenceRandomViewModelInterface {
    val randomSentence: StateFlow<SentenceItem?>
    val isLoading: StateFlow<Boolean>
    
    fun fetchRandomSentenceByLevel(level: String?)
} 