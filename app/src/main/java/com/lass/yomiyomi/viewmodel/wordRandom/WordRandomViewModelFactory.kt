package com.lass.yomiyomi.viewmodel.wordRandom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.WordRepository

class WordRandomViewModelFactory(private val wordRepository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordRandomViewModel::class.java)) {
            return WordRandomViewModel(wordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}