package com.lass.yomiyomi.viewmodel.kanjiRandom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.KanjiItem
import com.lass.yomiyomi.data.repository.KanjiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KanjiRandomRandomViewModel @Inject constructor(
    private val repository: KanjiRepository
) : ViewModel(), KanjiRandomViewModelInterface {
    private val _randomKanji = MutableStateFlow<KanjiItem?>(null)
    override val randomKanji: StateFlow<KanjiItem?> = _randomKanji

    override fun fetchRandomKanji() {
        viewModelScope.launch {
            _randomKanji.value = repository.getRandomKanji()
        }
    }

    override fun fetchRandomKanjiByLevel(level: String?) {
        viewModelScope.launch {
            _randomKanji.value = repository.getRandomKanjiByLevel(level)
        }
    }
}