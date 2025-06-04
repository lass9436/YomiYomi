package com.lass.yomiyomi.viewmodel.myParagraph.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphRandomViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MyParagraphRandomViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _currentParagraph = MutableStateFlow<ParagraphItem?>(null)
    override val currentParagraph: StateFlow<ParagraphItem?> = _currentParagraph.asStateFlow()

    private val _currentSentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    override val currentSentences: StateFlow<List<SentenceItem>> = _currentSentences.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun loadRandomParagraph() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val randomParagraph = if (_selectedLevel.value == Level.ALL) {
                    myParagraphRepository.getRandomParagraph()
                } else {
                    myParagraphRepository.getRandomParagraphByLevel(_selectedLevel.value.value)
                }

                _currentParagraph.value = randomParagraph

                // 문단에 속한 문장들도 함께 로드
                if (randomParagraph != null) {
                    val sentences = mySentenceRepository.getSentencesByParagraph(randomParagraph.paragraphId)
                    _currentSentences.value = sentences
                } else {
                    _currentSentences.value = emptyList()
                }
            } catch (e: Exception) {
                _currentParagraph.value = null
                _currentSentences.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun updateSentenceLearningProgress(id: Int, progress: Float) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateLearningProgress(id, progress)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
