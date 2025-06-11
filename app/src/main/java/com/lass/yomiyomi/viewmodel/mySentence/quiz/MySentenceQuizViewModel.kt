package com.lass.yomiyomi.viewmodel.mySentence.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuiz
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.tts.ForegroundTTSManager
import com.lass.yomiyomi.util.JapaneseTextFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceQuizViewModel @Inject constructor(
    private val mySentenceRepository: MySentenceRepository,
    private val foregroundTTSManager: ForegroundTTSManager
) : ViewModel(), MySentenceQuizViewModelInterface {

    private val _quizState = MutableStateFlow<SentenceQuiz?>(null)
    override val quizState: StateFlow<SentenceQuiz?> = _quizState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> = _hasInsufficientData.asStateFlow()

    override val isListening: StateFlow<Boolean> = foregroundTTSManager.isListening
    override val recognizedText: StateFlow<String> = foregroundTTSManager.recognizedText

    // 현재 문장 데이터를 저장 (퀴즈 타입 변경 시 재사용)
    private var currentSentence: SentenceItem? = null

    override fun loadQuizByLevel(level: Level, quizType: SentenceQuizType, isLearningMode: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                val sentences = if (level == Level.ALL) {
                    mySentenceRepository.getAllSentences()
                } else {
                    mySentenceRepository.getSentencesByLevel(level.value!!)
                }
                
                if (sentences.isEmpty()) {
                    _hasInsufficientData.value = true
                    _quizState.value = null
                } else {
                    val randomSentence = sentences.random()
                    currentSentence = randomSentence // 현재 문장 저장
                    val quiz = createQuizFromSentence(randomSentence, quizType)
                    _quizState.value = quiz
                }
            } catch (e: Exception) {
                _hasInsufficientData.value = true
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadQuizBySentenceId(sentenceId: Int, quizType: SentenceQuizType) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                val sentence = mySentenceRepository.getSentenceById(sentenceId)
                if (sentence != null) {
                    currentSentence = sentence
                    val quiz = createQuizFromSentence(sentence, quizType)
                    _quizState.value = quiz
                } else {
                    _hasInsufficientData.value = true
                    _quizState.value = null
                }
            } catch (e: Exception) {
                _hasInsufficientData.value = true
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun changeQuizType(quizType: SentenceQuizType) {
        // 현재 문장이 있으면 같은 문장으로 새로운 타입의 퀴즈 생성
        currentSentence?.let { sentence ->
            val quiz = createQuizFromSentence(sentence, quizType)
            _quizState.value = quiz
        }
    }

    private fun createQuizFromSentence(sentence: SentenceItem, quizType: SentenceQuizType): SentenceQuiz {
        return when (quizType) {
            SentenceQuizType.KOREAN_TO_JAPANESE_SPEECH -> {
                SentenceQuiz(
                    question = sentence.korean,
                    correctAnswer = sentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(sentence.japanese),
                    sentenceId = sentence.id
                )
            }
            SentenceQuizType.JAPANESE_TO_JAPANESE_SPEECH -> {
                SentenceQuiz(
                    question = sentence.japanese,
                    correctAnswer = sentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(sentence.japanese),
                    sentenceId = sentence.id
                )
            }
            SentenceQuizType.JAPANESE_NO_FURIGANA_SPEECH -> {
                val questionWithoutFurigana = JapaneseTextFilter.removeFurigana(sentence.japanese)
                SentenceQuiz(
                    question = questionWithoutFurigana,
                    correctAnswer = sentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(sentence.japanese),
                    sentenceId = sentence.id
                )
            }
        }
    }

    override fun startListening() {
        foregroundTTSManager.startListening()
    }

    override fun stopListening() {
        foregroundTTSManager.stopListening()
    }

    override fun checkAnswer(recognizedAnswer: String): Boolean {
        val currentQuiz = _quizState.value ?: return false
        
        // 음성 인식 비교용으로 구두점, 띄어쓰기 등을 모두 제거하여 비교
        val normalizedCorrectAnswer = JapaneseTextFilter.normalizeForComparison(currentQuiz.correctAnswer)
        val normalizedRecognized = JapaneseTextFilter.normalizeForComparison(recognizedAnswer)
        
        // 정답 비교 (정규화된 텍스트로)
        val isCorrect = normalizedRecognized == normalizedCorrectAnswer
        
        // 학습 진도 업데이트 (정답인 경우)
        if (isCorrect) {
            viewModelScope.launch {
                try {
                    mySentenceRepository.updateLearningProgress(
                        currentQuiz.sentenceId, 
                        1.0f // 정답 시 100% 진도
                    )
                } catch (e: Exception) {
                    // 학습 진도 업데이트 실패는 무시
                }
            }
        }
        
        return isCorrect
    }

    override fun clearRecognizedText() {
        foregroundTTSManager.clearRecognizedText()
    }
} 
