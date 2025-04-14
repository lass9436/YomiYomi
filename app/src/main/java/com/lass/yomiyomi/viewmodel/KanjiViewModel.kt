package com.lass.yomiyomi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.repository.KanjiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KanjiViewModel(private val repository: KanjiRepository) : ViewModel(), KanjiViewModelInterface {
    private val _randomKanji = MutableStateFlow<Kanji?>(null)
    override val randomKanji: StateFlow<Kanji?> = _randomKanji

    override fun fetchRandomKanji() {
        viewModelScope.launch {
            _randomKanji.value = repository.getRandomKanji()
        }
    }
}