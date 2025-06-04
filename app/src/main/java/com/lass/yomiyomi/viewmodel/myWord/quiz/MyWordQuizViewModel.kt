package com.lass.yomiyomi.viewmodel.myWord.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.domain.usecase.GenerateMyWordQuizUseCase
import com.lass.yomiyomi.data.repository.MyWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyWordQuizViewModel @Inject constructor(
    private val generateMyWordQuizUseCase: GenerateMyWordQuizUseCase,
    private val myWordRepository: MyWordRepository
) : ViewModel(), MyWordQuizViewModelInterface {

    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> = _quizState

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> = _hasInsufficientData

    // 학습 모드를 위한 상태 관리
    private var priorityWordsInMemory: List<MyWordItem> = emptyList()
    private var distractorsInMemory: List<MyWordItem> = emptyList()
    private var currentPriorityIndex = 0
    private var currentWord: MyWordItem? = null

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _hasInsufficientData.value = false
                
                if (!isLearningMode) {
                    // 랜덤 모드
                    currentWord = null
                    val quiz = generateMyWordQuizUseCase.generateQuiz(level, quizType, false)
                    
                    if (quiz != null) {
                        _quizState.value = quiz
                    } else {
                        _hasInsufficientData.value = true
                        _quizState.value = null
                    }
                } else {
                    // 학습 모드
                    if (priorityWordsInMemory.isEmpty() || currentPriorityIndex >= priorityWordsInMemory.size) {
                        // 새로운 학습 데이터 로드
                        val (priorityWords, distractors) = myWordRepository.getMyWordsForLearningMode(level.value ?: "ALL")
                        priorityWordsInMemory = priorityWords
                        distractorsInMemory = distractors
                        currentPriorityIndex = 0
                    }
                    
                    if (priorityWordsInMemory.isEmpty()) {
                        // 학습 모드용 데이터가 없으면 랜덤 모드로 폴백
                        val quiz = generateMyWordQuizUseCase.generateQuiz(level, quizType, false)
                        if (quiz != null) {
                            _quizState.value = quiz
                        } else {
                            _hasInsufficientData.value = true
                            _quizState.value = null
                        }
                    } else {
                        // 현재 단어 저장
                        currentWord = priorityWordsInMemory[currentPriorityIndex]
                        currentPriorityIndex++
                        
                        // 퀴즈 생성
                        val quiz = generateMyWordQuizUseCase.generateQuiz(level, quizType, true)
                        if (quiz != null) {
                            _quizState.value = quiz
                        } else {
                            _hasInsufficientData.value = true
                            _quizState.value = null
                        }
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _hasInsufficientData.value = true
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean) {
        val currentQuiz = _quizState.value ?: return
        val isCorrect = selectedIndex == currentQuiz.correctIndex
        
        // 학습 모드일 때만 가중치 업데이트
        if (isLearningMode && currentWord != null) {
            viewModelScope.launch {
                myWordRepository.updateMyWordLearningStatus(
                    currentWord!!.id,
                    isCorrect,
                    currentWord!!.learningWeight
                )
            }
        }
    }
} 
