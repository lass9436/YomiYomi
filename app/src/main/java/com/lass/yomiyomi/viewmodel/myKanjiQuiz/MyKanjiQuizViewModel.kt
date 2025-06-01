package com.lass.yomiyomi.viewmodel.myKanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import com.lass.yomiyomi.domain.usecase.GenerateMyKanjiQuizUseCase
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyKanjiQuizViewModel @Inject constructor(
    private val generateMyKanjiQuizUseCase: GenerateMyKanjiQuizUseCase,
    private val myKanjiRepository: MyKanjiRepository
) : ViewModel(), MyKanjiQuizViewModelInterface {

    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> = _quizState

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> = _hasInsufficientData

    // 학습 모드를 위한 상태 관리
    private var priorityKanjiInMemory: List<MyKanji> = emptyList()
    private var distractorsInMemory: List<MyKanji> = emptyList()
    private var currentPriorityIndex = 0
    private var currentKanji: MyKanji? = null

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _hasInsufficientData.value = false
                
                if (!isLearningMode) {
                    // 랜덤 모드
                    currentKanji = null
                    val quiz = generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false)
                    
                    if (quiz != null) {
                        _quizState.value = quiz
                    } else {
                        _hasInsufficientData.value = true
                        _quizState.value = null
                    }
                } else {
                    // 학습 모드
                    if (priorityKanjiInMemory.isEmpty() || currentPriorityIndex >= priorityKanjiInMemory.size) {
                        // 새로운 학습 데이터 로드
                        val (priorityKanji, distractors) = myKanjiRepository.getMyKanjiForLearningMode(level.value ?: "ALL")
                        priorityKanjiInMemory = priorityKanji
                        distractorsInMemory = distractors
                        currentPriorityIndex = 0
                    }
                    
                    if (priorityKanjiInMemory.isEmpty()) {
                        // 학습 모드용 데이터가 없으면 랜덤 모드로 폴백
                        val quiz = generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false)
                        if (quiz != null) {
                            _quizState.value = quiz
                        } else {
                            _hasInsufficientData.value = true
                            _quizState.value = null
                        }
                    } else {
                        // 현재 한자 저장
                        currentKanji = priorityKanjiInMemory[currentPriorityIndex]
                        currentPriorityIndex++
                        
                        // 퀴즈 생성
                        val quiz = generateMyKanjiQuizUseCase.generateQuiz(level, quizType, true)
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
        if (isLearningMode && currentKanji != null) {
            viewModelScope.launch {
                myKanjiRepository.updateMyKanjiLearningStatus(
                    currentKanji!!.id,
                    isCorrect,
                    currentKanji!!.learningWeight
                )
            }
        }
    }
} 