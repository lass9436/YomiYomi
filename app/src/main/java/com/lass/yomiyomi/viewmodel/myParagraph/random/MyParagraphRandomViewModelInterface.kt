package com.lass.yomiyomi.viewmodel.myParagraph.random

import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphRandomViewModelInterface {
    val randomParagraph: StateFlow<ParagraphItem?>
    val sentences: StateFlow<List<SentenceItem>>
    val isLoading: StateFlow<Boolean>
    
    fun fetchRandomParagraphByLevel(level: String?)
} 