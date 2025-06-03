package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.data.KanjiQuiz
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.lass.yomiyomi.domain.model.entity.KanjiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KanjiQuizViewModel @Inject constructor(
    private val repository: KanjiRepository
) : ViewModel(), KanjiQuizViewModelInterface {
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> = _quizState

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    // 학습 모드를 위한 메모리 상태 관리
    private var priorityKanjiInMemory: List<KanjiItem> = emptyList()
    private var distractorsInMemory: List<KanjiItem> = emptyList()
    private var currentPriorityIndex = 0
    private var currentKanji: KanjiItem? = null

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                if (!isLearningMode) {
                    currentKanji = null
                    val quiz = generateRandomModeQuiz(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 학습 모드
                if (priorityKanjiInMemory.isEmpty() || currentPriorityIndex >= priorityKanjiInMemory.size) {
                    val (priorityKanji, distractors) = loadLearningModeData(level)
                    priorityKanjiInMemory = priorityKanji
                    distractorsInMemory = distractors
                    currentPriorityIndex = 0
                }

                // 우선순위 한자가 없으면 랜덤 모드로 폴백
                if (priorityKanjiInMemory.isEmpty()) {
                    currentKanji = null
                    val quiz = generateRandomModeQuiz(level, quizType)
                    _quizState.value = quiz
                    return@launch
                }

                // 현재 한자 저장
                currentKanji = priorityKanjiInMemory[currentPriorityIndex]
                
                // 퀴즈 생성
                val quiz = generateStudyModeQuiz(
                    currentKanji!!,
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

    // UseCase 로직을 ViewModel에 통합
    private suspend fun generateRandomModeQuiz(level: Level, quizType: KanjiQuizType): KanjiQuiz {
        // 정답 한자 가져오기
        val correctKanji = if (level == Level.ALL) {
            repository.getRandomKanji()
        } else {
            repository.getRandomKanjiByLevel(level.value)
        } ?: throw IllegalStateException("No kanji found for level: ${level.value}")
        
        // 오답용 한자들 가져오기 (같은 레벨에서 3개)
        val allKanji = if (level == Level.ALL) {
            repository.getAllKanji()
        } else {
            repository.getAllKanjiByLevel(level.value ?: "")
        }
        
        val distractors = allKanji.filter { it.id != correctKanji.id }.shuffled().take(3)
        
        if (distractors.size < 3) {
            throw IllegalStateException("Not enough kanji for quiz generation")
        }
        
        return generateStudyModeQuiz(correctKanji, distractors, quizType)
    }

    private suspend fun loadLearningModeData(level: Level): Pair<List<KanjiItem>, List<KanjiItem>> {
        return repository.getKanjiForLearningMode(level.toString())
    }

    private fun generateStudyModeQuiz(correctKanji: KanjiItem, distractors: List<KanjiItem>, quizType: KanjiQuizType): KanjiQuiz {
        // 오답 3개 선택 (매번 다르게)
        val wrongOptions = distractors.shuffled().take(3)
        
        // 4개의 보기를 만들고 섞기
        val allOptions = (wrongOptions + correctKanji).shuffled()
        
        return KanjiQuiz(
            question = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> correctKanji.kanji
                KanjiQuizType.READING_MEANING_TO_KANJI -> "${correctKanji.kunyomi} / ${correctKanji.meaning}"
            },
            answer = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> "${correctKanji.kunyomi} / ${correctKanji.meaning}"
                KanjiQuizType.READING_MEANING_TO_KANJI -> correctKanji.kanji
            },
            options = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> allOptions.map { "${it.kunyomi} / ${it.meaning}" }
                KanjiQuizType.READING_MEANING_TO_KANJI -> allOptions.map { it.kanji }
            },
            correctIndex = allOptions.indexOf(correctKanji)
        )
    }
}
