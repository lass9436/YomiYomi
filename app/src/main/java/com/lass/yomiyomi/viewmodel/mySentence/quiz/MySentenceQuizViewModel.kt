package com.lass.yomiyomi.viewmodel.mySentence.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuizState
import com.lass.yomiyomi.data.repository.MySentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceQuizViewModel @Inject constructor(
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MySentenceQuizViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _quizState = MutableStateFlow(SentenceQuizState())
    override val quizState: StateFlow<SentenceQuizState> = _quizState.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    private var quizSentences: List<SentenceItem> = emptyList()
    private val questionCount = 10 // 퀴즈 문제 수

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun startQuiz() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sentences = if (_selectedLevel.value == Level.ALL) {
                    mySentenceRepository.getIndividualSentences()
                } else {
                    mySentenceRepository.getIndividualSentencesByLevel(_selectedLevel.value.value ?: "ALL")
                }
                
                if (sentences.isNotEmpty()) {
                    quizSentences = sentences.shuffled().take(questionCount)
                    _quizState.value = SentenceQuizState(
                        currentQuestion = quizSentences.first(),
                        currentQuestionIndex = 0,
                        totalQuestions = quizSentences.size,
                        score = 0,
                        isAnswered = false,
                        isQuizFinished = false,
                        showAnswer = false
                    )
                } else {
                    // 문장이 없는 경우
                    _quizState.value = SentenceQuizState(isQuizFinished = true)
                }
            } catch (e: Exception) {
                _quizState.value = SentenceQuizState(isQuizFinished = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun showAnswer() {
        val currentState = _quizState.value
        _quizState.value = currentState.copy(showAnswer = true)
    }

    override fun answerCorrect() {
        val currentState = _quizState.value
        if (!currentState.isAnswered) {
            _quizState.value = currentState.copy(
                score = currentState.score + 1,
                isAnswered = true
            )
            
            // 학습 진도 업데이트
            currentState.currentQuestion?.let { sentence ->
                updateLearningProgress(sentence.id, 1.0f)
            }
        }
    }

    override fun answerIncorrect() {
        val currentState = _quizState.value
        if (!currentState.isAnswered) {
            _quizState.value = currentState.copy(isAnswered = true)
            
            // 틀린 경우에도 진도는 소폭 증가
            currentState.currentQuestion?.let { sentence ->
                val newProgress = (sentence.learningProgress + 0.1f).coerceAtMost(1.0f)
                updateLearningProgress(sentence.id, newProgress)
            }
        }
    }

    override fun nextQuestion() {
        val currentState = _quizState.value
        val nextIndex = currentState.currentQuestionIndex + 1
        
        if (nextIndex < quizSentences.size) {
            _quizState.value = currentState.copy(
                currentQuestion = quizSentences[nextIndex],
                currentQuestionIndex = nextIndex,
                isAnswered = false,
                showAnswer = false
            )
        } else {
            _quizState.value = currentState.copy(isQuizFinished = true)
        }
    }

    override fun resetQuiz() {
        _quizState.value = SentenceQuizState()
        quizSentences = emptyList()
    }

    private fun updateLearningProgress(id: Int, progress: Float) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateLearningProgress(id, progress)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 
