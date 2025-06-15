package com.lass.yomiyomi.viewmodel.myParagraph.list

import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphListViewModelInterface {
    val paragraphs: StateFlow<List<ParagraphItem>>
    val isLoading: StateFlow<Boolean>
    val selectedCategory: StateFlow<String>

    fun setSelectedCategory(category: String)
    fun searchParagraphs(query: String)
} 
