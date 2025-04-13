package com.lass.yomiyomi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.KanjiRepository

class KanjiViewModelFactory(private val kanjiRepository: KanjiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiViewModel::class.java)) {
            return KanjiViewModel(kanjiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
