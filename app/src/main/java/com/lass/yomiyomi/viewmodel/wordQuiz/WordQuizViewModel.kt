package com.lass.yomiyomi.viewmodel.wordQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
import com.lass.yomiyomi.domain.usecase.GenerateWordQuizRandomModeUseCase
import com.lass.yomiyomi.domain.usecase.GenerateWordQuizStudyModeUseCase
import com.lass.yomiyomi.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordQuizViewModel @Inject constructor(
    private val generateWordQuizRandomModeUseCase: GenerateWordQuizRandomModeUseCase,
    private val generateWordQuizStudyModeUseCase: GenerateWordQuizStudyModeUseCase,
    private val repository: WordRepository
) : ViewModel(), WordQuizViewModelInterface {

    // StateFlow로 퀴즈 데이터를 관리
    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> get() = _quizState

    // 퀴즈 로딩 상태를 관리
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    // 학습 모드를 위한 메모리 상태 관리
    private var priorityWordsInMemory: List<Word> = emptyList()
    private var distractorsInMemory: List<Word> = emptyList()
    private var currentPriorityIndex = 0
    private var currentQuizWord: Word? = null

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                if (!isLearningMode) {
                    currentQuizWord = null
                    val quiz = generateWordQuizRandomModeUseCase(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 학습 모드
                if (priorityWordsInMemory.isEmpty() || currentPriorityIndex >= priorityWordsInMemory.size) {
                    val (priorityWords, distractors) = generateWordQuizStudyModeUseCase.loadLearningModeData(level)
                    priorityWordsInMemory = priorityWords
                    distractorsInMemory = distractors
                    currentPriorityIndex = 0
                }

                // 현재 단어 저장
                currentQuizWord = priorityWordsInMemory[currentPriorityIndex]
                
                // 퀴즈 생성
                val quiz = generateWordQuizStudyModeUseCase.generateQuiz(
                    currentQuizWord!!,
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
        if (isLearningMode && currentQuizWord != null) {
            viewModelScope.launch {
                repository.updateWordLearningStatus(
                    currentQuizWord!!.id,
                    isCorrect,
                    currentQuizWord!!.learningWeight
                )
            }
        }
    }
}
