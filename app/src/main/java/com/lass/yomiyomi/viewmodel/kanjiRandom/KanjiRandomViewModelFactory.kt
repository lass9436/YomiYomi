package com.lass.yomiyomi.viewmodel.kanjiRandom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.KanjiRepository

class KanjiRandomViewModelFactory(private val kanjiRepository: KanjiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiRandomRandomViewModel::class.java)) {
            return KanjiRandomRandomViewModel(kanjiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
