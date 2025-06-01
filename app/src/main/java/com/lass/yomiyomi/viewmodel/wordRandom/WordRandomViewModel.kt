package com.lass.yomiyomi.viewmodel.wordRandom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Word
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
    private val _randomWord = MutableStateFlow<Word?>(null)
    override val randomWord: StateFlow<Word?> = _randomWord

    override fun fetchRandomWord() {
        viewModelScope.launch {
            _randomWord.value = repository.getRandomWord()
        }
    }

    override fun fetchRandomWordByLevel(level: String?) {
        viewModelScope.launch {
            _randomWord.value = repository.getRandomWordByLevel(level)
        }
    }
}