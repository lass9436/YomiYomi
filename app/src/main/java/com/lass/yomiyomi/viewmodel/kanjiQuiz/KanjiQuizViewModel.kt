package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizRandomModeUseCase
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizStudyModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.lass.yomiyomi.data.model.Kanji

class KanjiQuizViewModel(
    private val generateKanjiQuizRandomModeUseCase: GenerateKanjiQuizRandomModeUseCase,
    private val generateKanjiQuizStudyModeUseCase: GenerateKanjiQuizStudyModeUseCase,
    private val repository: KanjiRepository
) : ViewModel(), KanjiQuizViewModelInterface {
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> = _quizState

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    // 학습 모드를 위한 메모리 상태 관리
    private var priorityKanjiInMemory: List<Kanji> = emptyList()
    private var distractorsInMemory: List<Kanji> = emptyList()
    private var currentPriorityIndex = 0
    private var currentKanji: Kanji? = null

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                if (!isLearningMode) {
                    currentKanji = null
                    val quiz = generateKanjiQuizRandomModeUseCase(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 학습 모드
                if (priorityKanjiInMemory.isEmpty() || currentPriorityIndex >= priorityKanjiInMemory.size) {
                    val (priorityKanji, distractors) = generateKanjiQuizStudyModeUseCase.loadLearningModeData(level)
                    priorityKanjiInMemory = priorityKanji
                    distractorsInMemory = distractors
                    currentPriorityIndex = 0
                }

                // 현재 한자 저장
                currentKanji = priorityKanjiInMemory[currentPriorityIndex]
                
                // 퀴즈 생성
                val quiz = generateKanjiQuizStudyModeUseCase.generateQuiz(
                    currentKanji!!,
                    distractorsInMemory,
                    quizType
                )
                currentPriorityIndex++
                _quizState.value = quiz
                
            } catch (e: Exception) {
                e.printStackTrace()
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
                repository.updateKanjiLearningStatus(
                    currentKanji!!.id,
                    isCorrect,
                    currentKanji!!.learningWeight
                )
            }
        }
    }
}