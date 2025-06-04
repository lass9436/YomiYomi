package com.lass.yomiyomi.viewmodel.mySentence.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuiz
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.speech.SpeechManager
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
    private val speechManager: SpeechManager
) : ViewModel(), MySentenceQuizViewModelInterface {

    private val _quizState = MutableStateFlow<SentenceQuiz?>(null)
    override val quizState: StateFlow<SentenceQuiz?> = _quizState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> = _hasInsufficientData.asStateFlow()

    override val isListening: StateFlow<Boolean> = speechManager.isListening
    override val recognizedText: StateFlow<String> = speechManager.recognizedText

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
                    val quiz = createQuizFromSentences(sentences, quizType)
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

    private fun createQuizFromSentences(sentences: List<SentenceItem>, quizType: SentenceQuizType): SentenceQuiz {
        val randomSentence = sentences.random()
        
        return when (quizType) {
            SentenceQuizType.KOREAN_TO_JAPANESE_SPEECH -> {
                SentenceQuiz(
                    question = randomSentence.korean,
                    correctAnswer = randomSentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(randomSentence.japanese),
                    sentenceId = randomSentence.id
                )
            }
            SentenceQuizType.JAPANESE_TO_JAPANESE_SPEECH -> {
                SentenceQuiz(
                    question = randomSentence.japanese,
                    correctAnswer = randomSentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(randomSentence.japanese),
                    sentenceId = randomSentence.id
                )
            }
            SentenceQuizType.JAPANESE_NO_FURIGANA_SPEECH -> {
                val questionWithoutFurigana = JapaneseTextFilter.removeFurigana(randomSentence.japanese)
                SentenceQuiz(
                    question = questionWithoutFurigana,
                    correctAnswer = randomSentence.japanese,
                    cleanAnswer = JapaneseTextFilter.prepareTTSText(randomSentence.japanese),
                    sentenceId = randomSentence.id
                )
            }
        }
    }

    override fun startListening() {
        speechManager.startListening()
    }

    override fun stopListening() {
        speechManager.stopListening()
    }

    override fun checkAnswer(recognizedAnswer: String): Boolean {
        val currentQuiz = _quizState.value ?: return false
        
        // 인식된 텍스트를 정답 형식으로 변환
        val cleanRecognized = JapaneseTextFilter.prepareTTSText(recognizedAnswer)
        val cleanCorrect = currentQuiz.cleanAnswer
        
        // 정답 비교 (대소문자 무시, 공백 정규화)
        val isCorrect = cleanRecognized.lowercase().replace("\\s+".toRegex(), "") == 
                       cleanCorrect.lowercase().replace("\\s+".toRegex(), "")
        
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
        // SpeechManager에서 제공하지 않으면 여기서 관리
        // 현재는 SpeechManager가 자동으로 관리하므로 빈 구현
    }
} 