package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.data.BlankItem
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.util.ParagraphQuizGenerator
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.util.JapaneseTextFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphQuizViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository,
    private val speechManager: SpeechManager
) : ViewModel(), MyParagraphQuizViewModelInterface {

    // 퀴즈 상태
    private val _quizState = MutableStateFlow<ParagraphQuiz?>(null)
    override val quizState: StateFlow<ParagraphQuiz?> = _quizState.asStateFlow()

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 데이터 부족 상태
    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> = _hasInsufficientData.asStateFlow()

    // 음성 인식 상태
    private val _isListening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    // 인식된 텍스트
    private val _recognizedText = MutableStateFlow("")
    override val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    // 퀴즈 완료 상태
    private val _isQuizCompleted = MutableStateFlow(false)
    override val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted.asStateFlow()

    // 현재 문장들 (문장별 표시용)
    private val _sentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    override val sentences: StateFlow<List<SentenceItem>> = _sentences.asStateFlow()

    // 현재 단일 문장 (SingleSentenceQuiz용)
    private val _currentSentence = MutableStateFlow<SentenceItem?>(null)
    override val currentSentence: StateFlow<SentenceItem?> = _currentSentence.asStateFlow()

    // 현재 문단 (새로고침 시 같은 문단 유지용)
    private var currentParagraph: ParagraphItem? = null

    init {
        setupSpeechManager()
    }

    private fun setupSpeechManager() {
        viewModelScope.launch {
            speechManager.recognizedText.collect { result ->
                _recognizedText.value = result
                _isListening.value = false
                
                // 음성 인식이 완료되고 텍스트가 있으면 자동으로 정답 확인
                if (result.isNotEmpty()) {
                    processRecognizedText(result)
                }
            }
        }
        
        viewModelScope.launch {
            speechManager.isListening.collect { listening ->
                _isListening.value = listening
            }
        }
    }

    override fun loadQuizByLevel(level: Level, quizType: ParagraphQuizType) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                // 레벨에 맞는 문단들 가져오기
                val levelValue = level.value ?: "ALL"
                val paragraphs = if (levelValue == "ALL") {
                    myParagraphRepository.getAllParagraphs()
                } else {
                    myParagraphRepository.getParagraphsByLevel(levelValue)
                }

                if (paragraphs.isEmpty()) {
                    _hasInsufficientData.value = true
                    _quizState.value = null
                    return@launch
                }

                // 랜덤 문단 선택
                val selectedParagraph = paragraphs.random()
                currentParagraph = selectedParagraph

                // 문단에 속한 문장들 가져오기
                val sentences = mySentenceRepository.getSentencesByParagraph(selectedParagraph.paragraphId)
                
                if (sentences.isEmpty()) {
                    _hasInsufficientData.value = true
                    _quizState.value = null
                    return@launch
                }

                // 문장들을 별도로 저장
                _sentences.value = sentences.sortedBy { it.orderInParagraph }

                // 문장들을 하나의 긴 텍스트로 합치기 (일본어 텍스트 사용)
                val combinedJapaneseText = sentences.joinToString("\n") { it.japanese }
                val combinedKoreanText = sentences.joinToString("\n") { it.korean }

                // 퀴즈 생성
                val quiz = ParagraphQuizGenerator.generateParagraphQuiz(
                    paragraphId = selectedParagraph.paragraphId,
                    paragraphTitle = selectedParagraph.title,
                    japaneseText = combinedJapaneseText,
                    koreanText = combinedKoreanText,
                    quizType = quizType
                )

                _quizState.value = quiz
                _isQuizCompleted.value = false
                clearRecognizedText()

            } catch (e: Exception) {
                e.printStackTrace()
                _hasInsufficientData.value = true
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadQuizBySentence(sentence: SentenceItem, quizType: ParagraphQuizType) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                // 단일 문장으로 퀴즈 생성
                val quiz = ParagraphQuizGenerator.generateParagraphQuiz(
                    paragraphId = "single_sentence_${sentence.id}",
                    paragraphTitle = "문장 퀴즈",
                    japaneseText = sentence.japanese,
                    koreanText = sentence.korean,
                    quizType = quizType
                )

                // 문장을 리스트로 감싸서 저장
                _sentences.value = listOf(sentence)
                _currentSentence.value = sentence
                _quizState.value = quiz
                _isQuizCompleted.value = false
                clearRecognizedText()

            } catch (e: Exception) {
                e.printStackTrace()
                _hasInsufficientData.value = true
                _quizState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadQuizBySentenceId(sentenceId: Int, quizType: ParagraphQuizType) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                // sentenceId로 문장 조회
                val sentence = mySentenceRepository.getSentenceById(sentenceId)
                if (sentence != null) {
                    loadQuizBySentence(sentence, quizType)
                } else {
                    _hasInsufficientData.value = true
                    _quizState.value = null
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

    override fun startListening() {
        if (!_isListening.value) {
            speechManager.startListening()
        }
    }

    override fun stopListening() {
        if (_isListening.value) {
            speechManager.stopListening()
        }
    }

    override fun processRecognizedText(recognizedAnswer: String): List<String> {
        val quiz = _quizState.value ?: return emptyList()
        
        // ParagraphQuizGenerator의 fillBlanks 메서드 사용
        val newlyFilled = ParagraphQuizGenerator.fillBlanks(quiz, recognizedAnswer)
        
        // 완료 여부 체크
        val isCompleted = ParagraphQuizGenerator.isQuizCompleted(quiz)
        _isQuizCompleted.value = isCompleted
        
        // 상태 업데이트 (새로운 객체로 교체하여 recomposition 트리거)
        _quizState.value = quiz.copy()
        
        return newlyFilled
    }

    override fun resetQuiz() {
        val quiz = _quizState.value ?: return
        
        // 모든 빈칸을 초기화
        quiz.filledBlanks.clear()
        _isQuizCompleted.value = false
        _quizState.value = quiz.copy()
    }

    override fun showAllAnswers() {
        val quiz = _quizState.value ?: return
        
        // 모든 빈칸을 정답으로 채우기
        quiz.blanks.forEach { blank ->
            quiz.filledBlanks[blank.index] = blank.correctAnswer
        }
        
        // 퀴즈 완료 상태로 설정
        _isQuizCompleted.value = true
        _quizState.value = quiz.copy()
    }

    override fun clearRecognizedText() {
        _recognizedText.value = ""
        speechManager.clearRecognizedText()
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
} 