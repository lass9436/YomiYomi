package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.ParagraphQuizState
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphQuizViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MyParagraphQuizViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _quizState = MutableStateFlow(ParagraphQuizState())
    override val quizState: StateFlow<ParagraphQuizState> = _quizState.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    private var quizParagraphs: List<ParagraphItem> = emptyList()
    private val paragraphCount = 5 // 퀴즈 문단 수

    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun startQuiz() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paragraphs = if (_selectedLevel.value == Level.ALL) {
                    myParagraphRepository.getAllParagraphs()
                } else {
                    myParagraphRepository.getParagraphsByLevel(_selectedLevel.value.value ?: "ALL")
                }
                
                if (paragraphs.isNotEmpty()) {
                    quizParagraphs = paragraphs.shuffled().take(paragraphCount)
                    loadFirstParagraph()
                } else {
                    // 문단이 없는 경우
                    _quizState.value = ParagraphQuizState(isQuizFinished = true)
                }
            } catch (e: Exception) {
                _quizState.value = ParagraphQuizState(isQuizFinished = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadFirstParagraph() {
        if (quizParagraphs.isNotEmpty()) {
            val firstParagraph = quizParagraphs.first()
            val sentences = mySentenceRepository.getSentencesByParagraph(firstParagraph.paragraphId)
            
            _quizState.value = ParagraphQuizState(
                currentParagraph = firstParagraph,
                currentSentences = sentences,
                currentSentenceIndex = 0,
                currentParagraphIndex = 0,
                totalParagraphs = quizParagraphs.size,
                score = 0,
                isAnswered = false,
                isQuizFinished = false,
                showAnswer = false
            )
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
            
            // 현재 문장의 학습 진도 업데이트
            if (currentState.currentSentenceIndex < currentState.currentSentences.size) {
                val currentSentence = currentState.currentSentences[currentState.currentSentenceIndex]
                updateSentenceLearningProgress(currentSentence.id, 1.0f)
            }
        }
    }

    override fun answerIncorrect() {
        val currentState = _quizState.value
        if (!currentState.isAnswered) {
            _quizState.value = currentState.copy(isAnswered = true)
            
            // 틀린 경우에도 진도는 소폭 증가
            if (currentState.currentSentenceIndex < currentState.currentSentences.size) {
                val currentSentence = currentState.currentSentences[currentState.currentSentenceIndex]
                val newProgress = (currentSentence.learningProgress + 0.1f).coerceAtMost(1.0f)
                updateSentenceLearningProgress(currentSentence.id, newProgress)
            }
        }
    }

    override fun nextQuestion() {
        viewModelScope.launch {
            val currentState = _quizState.value
            val nextSentenceIndex = currentState.currentSentenceIndex + 1
            
            // 같은 문단 내 다음 문장으로 이동
            if (nextSentenceIndex < currentState.currentSentences.size) {
                _quizState.value = currentState.copy(
                    currentSentenceIndex = nextSentenceIndex,
                    isAnswered = false,
                    showAnswer = false
                )
            } else {
                // 다음 문단으로 이동
                val nextParagraphIndex = currentState.currentParagraphIndex + 1
                
                if (nextParagraphIndex < quizParagraphs.size) {
                    val nextParagraph = quizParagraphs[nextParagraphIndex]
                    val nextSentences = mySentenceRepository.getSentencesByParagraph(nextParagraph.paragraphId)
                    
                    _quizState.value = currentState.copy(
                        currentParagraph = nextParagraph,
                        currentSentences = nextSentences,
                        currentSentenceIndex = 0,
                        currentParagraphIndex = nextParagraphIndex,
                        isAnswered = false,
                        showAnswer = false
                    )
                } else {
                    _quizState.value = currentState.copy(isQuizFinished = true)
                }
            }
        }
    }

    override fun resetQuiz() {
        _quizState.value = ParagraphQuizState()
        quizParagraphs = emptyList()
    }

    override fun getCurrentSentence(): SentenceItem? {
        val currentState = _quizState.value
        return if (currentState.currentSentenceIndex < currentState.currentSentences.size) {
            currentState.currentSentences[currentState.currentSentenceIndex]
        } else {
            null
        }
    }

    private fun updateSentenceLearningProgress(id: Int, progress: Float) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateLearningProgress(id, progress)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 
