package com.lass.yomiyomi.viewmodel.myParagraph

import com.lass.yomiyomi.domain.model.ParagraphItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphViewModelInterface {
    val paragraphs: StateFlow<List<ParagraphItem>>
    val isLoading: StateFlow<Boolean>
    val selectedCategory: StateFlow<String>

    fun setSelectedCategory(category: String)
    fun searchParagraphs(query: String)
} 