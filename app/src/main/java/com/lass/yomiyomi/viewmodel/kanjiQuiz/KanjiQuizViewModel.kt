package com.lass.yomiyomi.viewmodel.kanjiQuiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KanjiQuizViewModel(application: Application) : AndroidViewModel(application), KanjiQuizViewModelInterface {
    private val repository = KanjiRepository(application)
    private val generateKanjiQuizUseCase = GenerateKanjiQuizUseCase(repository)

    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> = _quizState

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private var currentKanjiId: Int? = null

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val quiz = generateKanjiQuizUseCase.execute(level, quizType, isLearningMode)
                _quizState.value = quiz.first
                currentKanjiId = quiz.second
            } catch (e: Exception) {
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean) {
        if (!isLearningMode || currentKanjiId == null) return
        
        viewModelScope.launch {
            val isCorrect = selectedIndex == _quizState.value?.correctIndex
            repository.updateKanjiLearningStatus(currentKanjiId!!, isCorrect, 0.5f) // 초기 가중치는 0.5f
        }
    }
}