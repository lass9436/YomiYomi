package com.lass.yomiyomi.viewmodel.word.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordQuizViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel(), WordQuizViewModelInterface {

    // StateFlow로 퀴즈 데이터를 관리
    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> get() = _quizState

    // 퀴즈 로딩 상태를 관리
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    // 학습 모드를 위한 메모리 상태 관리
    private var priorityWordsInMemory: List<WordItem> = emptyList()
    private var distractorsInMemory: List<WordItem> = emptyList()
    private var currentPriorityIndex = 0
    private var currentQuizWord: WordItem? = null

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                if (!isLearningMode) {
                    currentQuizWord = null
                    val quiz = generateRandomModeQuiz(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 학습 모드
                if (priorityWordsInMemory.isEmpty() || currentPriorityIndex >= priorityWordsInMemory.size) {
                    val (priorityWords, distractors) = loadLearningModeData(level)
                    priorityWordsInMemory = priorityWords
                    distractorsInMemory = distractors
                    currentPriorityIndex = 0
                }

                // 우선순위 단어가 없으면 랜덤 모드로 폴백
                if (priorityWordsInMemory.isEmpty()) {
                    currentQuizWord = null
                    val quiz = generateRandomModeQuiz(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 현재 단어 저장
                currentQuizWord = priorityWordsInMemory[currentPriorityIndex]
                
                // 퀴즈 생성
                val quiz = generateStudyModeQuiz(
                    currentQuizWord!!,
                    distractorsInMemory,
                    quizType
                )
                currentPriorityIndex++
                _quizState.value = quiz
                
            } catch (e: Exception) {
                e.printStackTrace()
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
    
    // UseCase 로직을 ViewModel에 통합
    private suspend fun generateRandomModeQuiz(level: Level, quizType: WordQuizType): WordQuiz {
        // 정답 단어 가져오기
        val correctWord = if (level == Level.ALL) {
            repository.getRandomWord()
        } else {
            repository.getRandomWordByLevel(level.value)
        } ?: throw IllegalStateException("No word found for level: ${level.value}")
        
        // 오답용 단어들 가져오기 (같은 레벨에서 3개)
        val allWords = if (level == Level.ALL) {
            repository.getAllWords()
        } else {
            repository.getAllWordsByLevel(level.value ?: "")
        }
        
        val distractors = allWords.filter { it.id != correctWord.id }.shuffled().take(3)
        
        if (distractors.size < 3) {
            throw IllegalStateException("Not enough words for quiz generation")
        }
        
        return generateStudyModeQuiz(correctWord, distractors, quizType)
    }

    private suspend fun loadLearningModeData(level: Level): Pair<List<WordItem>, List<WordItem>> {
        return repository.getWordsForLearningMode(level.toString())
    }

    private suspend fun generateStudyModeQuiz(correctWord: WordItem, distractors: List<WordItem>, quizType: WordQuizType): WordQuiz {
        // 오답 3개 선택 (매번 다르게)
        var wrongOptions = distractors.filter { it.id != correctWord.id }.shuffled().take(3)
        
        // 만약 distractors가 3개 미만이면 전체 단어에서 추가로 가져오기
        if (wrongOptions.size < 3) {
            val allWords = repository.getAllWords()
            val additionalOptions = allWords
                .filter { it.id != correctWord.id && !wrongOptions.any { existing -> existing.id == it.id } }
                .shuffled()
                .take(3 - wrongOptions.size)
            wrongOptions = wrongOptions + additionalOptions
        }
        
        // 4개의 보기를 만들고 섞기
        val allOptions = (wrongOptions + correctWord).shuffled()
        
        return WordQuiz(
            question = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> correctWord.word
                WordQuizType.MEANING_READING_TO_WORD -> "${correctWord.meaning} / ${correctWord.reading}"
            },
            answer = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> "${correctWord.meaning} / ${correctWord.reading}"
                WordQuizType.MEANING_READING_TO_WORD -> correctWord.word
            },
            options = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> allOptions.map { "${it.meaning} / ${it.reading}" }
                WordQuizType.MEANING_READING_TO_WORD -> allOptions.map { it.word }
            },
            correctIndex = allOptions.indexOf(correctWord)
        )
    }
}
