package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import com.lass.yomiyomi.ui.layout.SentenceQuizLayout
import com.lass.yomiyomi.ui.state.SentenceQuizCallbacks
import com.lass.yomiyomi.ui.state.SentenceQuizState
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModel
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModelInterface
import com.lass.yomiyomi.util.JapaneseTextFilter

@Composable
fun SingleSentenceQuizScreen(
    sentenceId: Int,
    onBack: () -> Unit,
    mySentenceQuizViewModel: MySentenceQuizViewModelInterface = hiltViewModel<MySentenceQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { 
        mySentenceQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
        onBack() 
    }

    // ViewModel 상태 수집
    val quizData by mySentenceQuizViewModel.quizState.collectAsState()
    val isLoading by mySentenceQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData by mySentenceQuizViewModel.hasInsufficientData.collectAsState()
    val isListening by mySentenceQuizViewModel.isListening.collectAsState()
    val recognizedText by mySentenceQuizViewModel.recognizedText.collectAsState()

    // UI 상태 관리
    var selectedQuizTypeIndex by remember { mutableStateOf(0) }
    var isLearningMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var answerResult by remember { mutableStateOf<String?>(null) }

    val quizTypes = listOf("한국어", "일본어", "요미가나X")
    val sentenceQuizTypes = listOf(
        SentenceQuizType.KOREAN_TO_JAPANESE_SPEECH,
        SentenceQuizType.JAPANESE_TO_JAPANESE_SPEECH,
        SentenceQuizType.JAPANESE_NO_FURIGANA_SPEECH
    )

    // 초기 퀴즈 로드 (특정 문장 ID로)
    LaunchedEffect(sentenceId, selectedQuizTypeIndex) {
        mySentenceQuizViewModel.loadQuizBySentenceId(sentenceId, sentenceQuizTypes[selectedQuizTypeIndex])
    }

    // Quiz state 생성
    val state = SentenceQuizState(
        selectedLevel = com.lass.yomiyomi.domain.model.constant.Level.ALL, // 단일 문장이므로 레벨은 의미 없음
        quizTypes = quizTypes,
        selectedQuizTypeIndex = selectedQuizTypeIndex,
        isLearningMode = isLearningMode,
        isLoading = isLoading,
        question = quizData?.question,
        showDialog = showDialog,
        answerResult = answerResult,
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        insufficientDataMessage = if (hasInsufficientData) "문장을 불러올 수 없습니다." else null,
        isListening = isListening,
        recognizedText = recognizedText,
        availableLevels = emptyList() // 단일 문장이므로 레벨 선택 불필요
    )

    // Quiz callbacks 생성
    val callbacks = SentenceQuizCallbacks(
        onLevelSelected = { }, // 레벨 선택 불필요
        onQuizTypeSelected = { index ->
            selectedQuizTypeIndex = index
            // 퀴즈 타입만 변경 (새로운 문제 로드)
            mySentenceQuizViewModel.loadQuizBySentenceId(sentenceId, sentenceQuizTypes[index])
        },
        onLearningModeChanged = { learningMode ->
            isLearningMode = learningMode
            // 학습 모드는 현재 문장에서는 의미 없지만 UI 일관성을 위해 유지
        },
        onStartListening = {
            mySentenceQuizViewModel.startListening()
        },
        onStopListening = {
            mySentenceQuizViewModel.stopListening()
        },
        onCheckAnswer = { recognizedAnswer ->
            val isCorrect = mySentenceQuizViewModel.checkAnswer(recognizedAnswer)
            // 정답에서 후리가나 제거
            val cleanCorrectAnswer = JapaneseTextFilter.removeFurigana(quizData?.correctAnswer ?: "")
            answerResult = if (isCorrect) {
                "정답입니다! 🎉\n정답: $cleanCorrectAnswer"
            } else {
                "틀렸습니다. 😅\n정답: $cleanCorrectAnswer\n인식된 답: $recognizedAnswer"
            }
            showDialog = true
            mySentenceQuizViewModel.clearRecognizedText() // 정답 확인 후 인식된 텍스트 초기화
        },
        onRefresh = {
            // 새로고침은 같은 문장으로 다시 퀴즈 생성
            mySentenceQuizViewModel.loadQuizBySentenceId(sentenceId, sentenceQuizTypes[selectedQuizTypeIndex])
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
        }
    )

    SentenceQuizLayout(
        title = "문장 퀴즈 🧩",
        state = state.copy(availableLevels = emptyList()), // 레벨 선택기 숨기기
        callbacks = callbacks,
        onBack = {
            mySentenceQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
            onBack()
        }
    )
} 