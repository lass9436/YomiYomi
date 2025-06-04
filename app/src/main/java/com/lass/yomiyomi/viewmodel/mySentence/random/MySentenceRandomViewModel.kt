package com.lass.yomiyomi.viewmodel.mySentence.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceRandomViewModel @Inject constructor(
    private val mySentenceRepository: MySentenceRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _currentSentence = MutableStateFlow<SentenceItem?>(null)
    val currentSentence: StateFlow<SentenceItem?> = _currentSentence.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    fun loadRandomSentence() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val randomSentence = if (_selectedLevel.value == Level.ALL) {
                    mySentenceRepository.getRandomIndividualSentence()
                } else {
                    mySentenceRepository.getRandomIndividualSentenceByLevel(_selectedLevel.value.value)
                }
                _currentSentence.value = randomSentence
            } catch (e: Exception) {
                _currentSentence.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLearningProgress(id: Int, progress: Float) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateLearningProgress(id, progress)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
