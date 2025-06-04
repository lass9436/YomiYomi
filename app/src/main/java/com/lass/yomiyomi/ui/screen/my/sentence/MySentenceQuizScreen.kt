package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.SentenceQuizLayout
import com.lass.yomiyomi.ui.state.SentenceQuizCallbacks
import com.lass.yomiyomi.ui.state.SentenceQuizState
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModel
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModelInterface

@Composable
fun MySentenceQuizScreen(
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
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var selectedQuizTypeIndex by remember { mutableStateOf(0) }
    var isLearningMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var answerResult by remember { mutableStateOf<String?>(null) }

    val quizTypes = listOf("한국어 → 일어 음성", "일어 전체 → 일어 음성", "일어(후리가나X) → 일어 음성")
    val sentenceQuizTypes = listOf(
        SentenceQuizType.KOREAN_TO_JAPANESE_SPEECH,
        SentenceQuizType.JAPANESE_TO_JAPANESE_SPEECH,
        SentenceQuizType.JAPANESE_NO_FURIGANA_SPEECH
    )

    // Quiz state 생성
    val state = SentenceQuizState(
        selectedLevel = selectedLevel,
        quizTypes = quizTypes,
        selectedQuizTypeIndex = selectedQuizTypeIndex,
        isLearningMode = isLearningMode,
        isLoading = isLoading,
        question = quizData?.question,
        showDialog = showDialog,
        answerResult = answerResult,
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        insufficientDataMessage = if (hasInsufficientData) "퀴즈할 문장이 부족합니다. 더 많은 문장을 추가해주세요." else null,
        isListening = isListening,
        recognizedText = recognizedText
    )

    // Quiz callbacks 생성
    val callbacks = SentenceQuizCallbacks(
        onLevelSelected = { level ->
            selectedLevel = level
            mySentenceQuizViewModel.loadQuizByLevel(level, sentenceQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onQuizTypeSelected = { index ->
            selectedQuizTypeIndex = index
            mySentenceQuizViewModel.loadQuizByLevel(selectedLevel, sentenceQuizTypes[index], isLearningMode)
        },
        onLearningModeChanged = { learningMode ->
            isLearningMode = learningMode
            mySentenceQuizViewModel.loadQuizByLevel(selectedLevel, sentenceQuizTypes[selectedQuizTypeIndex], learningMode)
        },
        onStartListening = {
            mySentenceQuizViewModel.startListening()
        },
        onStopListening = {
            mySentenceQuizViewModel.stopListening()
        },
        onCheckAnswer = { recognizedAnswer ->
            val isCorrect = mySentenceQuizViewModel.checkAnswer(recognizedAnswer)
            answerResult = if (isCorrect) {
                "정답입니다! 🎉\n정답: ${quizData?.correctAnswer ?: ""}"
            } else {
                "틀렸습니다. 😅\n정답: ${quizData?.correctAnswer ?: ""}\n인식된 답: $recognizedAnswer"
            }
            showDialog = true
        },
        onRefresh = {
            mySentenceQuizViewModel.loadQuizByLevel(selectedLevel, sentenceQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            mySentenceQuizViewModel.stopListening() // 다이얼로그 닫을 때 음성 인식 중지
            mySentenceQuizViewModel.loadQuizByLevel(selectedLevel, sentenceQuizTypes[selectedQuizTypeIndex], isLearningMode)
        }
    )

    // 초기 퀴즈 로드
    LaunchedEffect(Unit) {
        mySentenceQuizViewModel.loadQuizByLevel(selectedLevel, sentenceQuizTypes[selectedQuizTypeIndex], isLearningMode)
    }

    SentenceQuizLayout(
        title = "내 문장 퀴즈 🎤",
        state = state,
        callbacks = callbacks,
        onBack = {
            mySentenceQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
            onBack()
        }
    )
} 