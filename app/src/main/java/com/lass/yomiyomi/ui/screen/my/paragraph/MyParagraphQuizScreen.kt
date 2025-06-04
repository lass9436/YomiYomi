package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.ParagraphQuizLayout
import com.lass.yomiyomi.ui.state.ParagraphQuizCallbacks
import com.lass.yomiyomi.ui.state.ParagraphQuizState
import com.lass.yomiyomi.viewmodel.myParagraph.quiz.MyParagraphQuizViewModel
import com.lass.yomiyomi.viewmodel.myParagraph.quiz.MyParagraphQuizViewModelInterface
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyParagraphQuizScreen(
    onBack: () -> Unit,
    myParagraphQuizViewModel: MyParagraphQuizViewModelInterface = hiltViewModel<MyParagraphQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { 
        myParagraphQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
        onBack() 
    }

    // ViewModel 상태 수집
    val quizData by myParagraphQuizViewModel.quizState.collectAsState()
    val isLoading by myParagraphQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData by myParagraphQuizViewModel.hasInsufficientData.collectAsState()
    val isListening by myParagraphQuizViewModel.isListening.collectAsState()
    val recognizedText by myParagraphQuizViewModel.recognizedText.collectAsState()
    val isQuizCompleted by myParagraphQuizViewModel.isQuizCompleted.collectAsState()
    val sentences by myParagraphQuizViewModel.sentences.collectAsState()

    // UI 상태 관리
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var showKoreanTranslation by remember { mutableStateOf(true) }

    // Quiz state 생성
    val state = ParagraphQuizState(
        selectedLevel = selectedLevel,
        isLoading = isLoading,
        quiz = quizData,
        insufficientDataMessage = if (hasInsufficientData) "퀴즈할 문단이 부족합니다. 더 많은 문단을 추가해주세요." else null,
        isListening = isListening,
        recognizedText = recognizedText,
        isQuizCompleted = isQuizCompleted,
        sentences = sentences,
        showKoreanTranslation = showKoreanTranslation
    )

    // Quiz callbacks 생성
    val callbacks = ParagraphQuizCallbacks(
        onLevelSelected = { level ->
            selectedLevel = level
            myParagraphQuizViewModel.loadQuizByLevel(level, ParagraphQuizType.FILL_IN_BLANKS_SPEECH)
        },
        onStartListening = {
            myParagraphQuizViewModel.startListening()
        },
        onStopListening = {
            myParagraphQuizViewModel.stopListening()
        },
        onProcessRecognition = { recognizedAnswer ->
            val newlyFilled = myParagraphQuizViewModel.processRecognizedText(recognizedAnswer)
            
            // 새로 채워진 빈칸이 있으면 나중에 인식된 텍스트 초기화 (결과를 보고 난 후)
            if (newlyFilled.isNotEmpty()) {
                // 3초 후에 텍스트 초기화 (사용자가 결과를 볼 시간을 줌)
                MainScope().launch {
                    delay(3000)
                    myParagraphQuizViewModel.clearRecognizedText()
                }
            }
            
            // UI에서 사용할 수 있도록 반환
            newlyFilled
        },
        onRefresh = {
            myParagraphQuizViewModel.loadQuizByLevel(selectedLevel, ParagraphQuizType.FILL_IN_BLANKS_SPEECH)
        },
        onResetQuiz = {
            myParagraphQuizViewModel.resetQuiz()
            myParagraphQuizViewModel.clearRecognizedText()
        },
        onShowAnswers = {
            myParagraphQuizViewModel.showAllAnswers()
            myParagraphQuizViewModel.clearRecognizedText()
        },
        onToggleKoreanTranslation = {
            showKoreanTranslation = !showKoreanTranslation
        }
    )

    // 초기 퀴즈 로드
    LaunchedEffect(Unit) {
        myParagraphQuizViewModel.loadQuizByLevel(selectedLevel, ParagraphQuizType.FILL_IN_BLANKS_SPEECH)
    }

    ParagraphQuizLayout(
        title = "내 문단 퀴즈 🧩",
        state = state,
        callbacks = callbacks,
        onBack = {
            myParagraphQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
            onBack()
        }
    )
} 