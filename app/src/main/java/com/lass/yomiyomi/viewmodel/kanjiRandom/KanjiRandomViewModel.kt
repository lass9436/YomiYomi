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
class KanjiRandomViewModel @Inject constructor(
    private val repository: KanjiRepository
) : ViewModel(), KanjiRandomViewModelInterface {
    private val _randomKanji = MutableStateFlow<KanjiItem?>(null)
    override val randomKanji: StateFlow<KanjiItem?> = _randomKanji

    override fun fetchRandomKanji() {
        viewModelScope.launch {
            try {
                _randomKanji.value = repository.getRandomKanji()
            } catch (e: Exception) {
                // Log error but don't crash - keep state as null
                e.printStackTrace()
                _randomKanji.value = null
            }
        }
    }

    override fun fetchRandomKanjiByLevel(level: String?) {
        viewModelScope.launch {
            try {
                _randomKanji.value = repository.getRandomKanjiByLevel(level)
            } catch (e: Exception) {
                // Log error but don't crash - keep state as null
                e.printStackTrace()
                _randomKanji.value = null
            }
        }
    }
}
