package com.lass.yomiyomi.viewmodel.word.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordRandomViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel(), WordRandomViewModelInterface {
    private val _randomWord = MutableStateFlow<WordItem?>(null)
    override val randomWord: StateFlow<WordItem?> = _randomWord

    override fun fetchRandomWord() {
        viewModelScope.launch {
            try {
                _randomWord.value = repository.getRandomWord()
            } catch (e: Exception) {
                // Log error but don't crash - keep state as null
                e.printStackTrace()
                _randomWord.value = null
            }
        }
    }

    override fun fetchRandomWordByLevel(level: String?) {
        viewModelScope.launch {
            try {
                _randomWord.value = repository.getRandomWordByLevel(level)
            } catch (e: Exception) {
                // Log error but don't crash - keep state as null
                e.printStackTrace()
                _randomWord.value = null
            }
        }
    }
}
