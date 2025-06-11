package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.util.ParagraphQuizGenerator
import com.lass.yomiyomi.media.MediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphQuizViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository,
    private val mediaManager: MediaManager
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
    override val isListening: StateFlow<Boolean> = mediaManager.isListening

    // 인식된 텍스트
    override val recognizedText: StateFlow<String> = mediaManager.recognizedText

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
        setupSpeechRecognitionManager()
    }

    private fun setupSpeechRecognitionManager() {
        viewModelScope.launch {
            recognizedText.collect { result ->
                if (result.isNotEmpty()) {
                    processRecognizedText(result)
                }
            }
        }
        viewModelScope.launch {
            isListening.collect { listening ->
                if (!listening) {
                    processRecognizedText(recognizedText.value)
                }
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
                    paragraphId = sentence.id,
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

    override fun loadQuizByParagraphId(paragraphId: Int, quizType: ParagraphQuizType) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _hasInsufficientData.value = false
            
            try {
                // 문단 ID로 문단 조회
                val paragraph = myParagraphRepository.getParagraphById(paragraphId)
                if (paragraph == null) {
                    _hasInsufficientData.value = true
                    _quizState.value = null
                    return@launch
                }
                
                currentParagraph = paragraph

                // 문단에 속한 문장들 가져오기
                val sentences = mySentenceRepository.getSentencesByParagraph(paragraphId)
                
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
                    paragraphId = paragraph.paragraphId,
                    paragraphTitle = paragraph.title,
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

    override fun startListening() {
        mediaManager.startListeningWithPolicy()
    }

    override fun stopListening() {
        mediaManager.stopListening()
    }

    override fun processRecognizedText(recognizedAnswer: String): List<String> {
        val quiz = _quizState.value ?: return emptyList()
        
        // ParagraphQuizGenerator의 fillBlanks 메서드 사용
        val newlyFilled = ParagraphQuizGenerator.fillBlanks(quiz, recognizedAnswer)
        
        // 🔥 새로 채워진 빈칸이 있으면 해당 문장들의 학습 진도 업데이트
        if (newlyFilled.isNotEmpty()) {
            updateLearningProgressForFilledBlanks(quiz, newlyFilled)
        }
        
        // 완료 여부 체크
        val isCompleted = ParagraphQuizGenerator.isQuizCompleted(quiz)
        _isQuizCompleted.value = isCompleted
        
        // 🎯 퀴즈가 완료되면 모든 문장의 학습 진도를 100%로 업데이트
        if (isCompleted) {
            updateLearningProgressForAllSentences()
        }
        
        // 상태 업데이트 (새로운 객체로 교체하여 recomposition 트리거)
        _quizState.value = quiz.copy()
        
        return newlyFilled
    }
    
    /**
     * 새로 채워진 빈칸에 해당하는 문장들의 학습 진도 업데이트
     */
    private fun updateLearningProgressForFilledBlanks(quiz: ParagraphQuiz, newlyFilled: List<String>) {
        viewModelScope.launch {
            try {
                val sentences = _sentences.value
                if (sentences.isEmpty()) return@launch
                
                // 각 문장별로 학습 진도 계산 및 업데이트
                sentences.forEach { sentence ->
                    updateSentenceLearningProgress(quiz, sentence)
                }
            } catch (e: Exception) {
                // 학습 진도 업데이트 실패는 무시 (로그는 출력)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 특정 문장의 학습 진도를 계산하여 업데이트
     */
    private suspend fun updateSentenceLearningProgress(quiz: ParagraphQuiz, sentence: SentenceItem) {
        // 해당 문장에 속한 빈칸들 찾기
        val sentenceBlanks = quiz.blanks.filter { blank ->
            // 빈칸의 정답이 해당 문장에 포함되어 있는지 확인
            sentence.japanese.contains(blank.correctAnswer)
        }
        
        if (sentenceBlanks.isEmpty()) return
        
        // 해당 문장에서 맞춘 빈칸 개수 계산
        val filledBlanksInSentence = sentenceBlanks.count { blank ->
            quiz.filledBlanks.containsKey(blank.index)
        }
        
        // 학습 진도 계산 (맞춘 빈칸 / 전체 빈칸)
        val progress = filledBlanksInSentence.toFloat() / sentenceBlanks.size.toFloat()
        
        // 학습 진도 업데이트
        mySentenceRepository.updateLearningProgress(sentence.id, progress)
        
        println("Debug - 문장 ${sentence.id}: ${filledBlanksInSentence}/${sentenceBlanks.size} = ${(progress * 100).toInt()}%")
    }
    
    /**
     * 퀴즈 완료 시 모든 문장의 학습 진도를 100%로 업데이트
     */
    private fun updateLearningProgressForAllSentences() {
        viewModelScope.launch {
            try {
                val sentences = _sentences.value
                sentences.forEach { sentence ->
                    mySentenceRepository.updateLearningProgress(
                        sentence.id, 
                        1.0f // 퀴즈 완료 시 100% 진도
                    )
                }
            } catch (e: Exception) {
                // 학습 진도 업데이트 실패는 무시 (로그는 출력)
                e.printStackTrace()
            }
        }
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
        mediaManager.clearRecognizedText()
    }

    override fun onCleared() {
        super.onCleared()
        mediaManager.stopForegroundAndRecognition()
    }
} 
