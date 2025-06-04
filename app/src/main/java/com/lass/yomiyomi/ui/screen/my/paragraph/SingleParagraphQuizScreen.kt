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
fun SingleParagraphQuizScreen(
    paragraphId: String,
    onBack: () -> Unit,
    myParagraphQuizViewModel: MyParagraphQuizViewModelInterface = hiltViewModel<MyParagraphQuizViewModel>()
) {
    // 백핸들러 등록
    BackHandler { onBack() }
    
    // State 수집
    val isLoading by myParagraphQuizViewModel.isLoading.collectAsState()
    val quiz by myParagraphQuizViewModel.quizState.collectAsState()
    val sentences by myParagraphQuizViewModel.sentences.collectAsState()
    val isListening by myParagraphQuizViewModel.isListening.collectAsState()
    val recognizedText by myParagraphQuizViewModel.recognizedText.collectAsState()
    val isQuizCompleted by myParagraphQuizViewModel.isQuizCompleted.collectAsState()
    val hasInsufficientData by myParagraphQuizViewModel.hasInsufficientData.collectAsState()
    
    var showKoreanTranslation by remember { mutableStateOf(true) }

    // 단일 문단 퀴즈 초기화
    LaunchedEffect(paragraphId) {
        myParagraphQuizViewModel.loadQuizByParagraphId(paragraphId)
    }

    val state = ParagraphQuizState(
        selectedLevel = Level.ALL, // 단일 문단에서는 레벨 고정
        isLoading = isLoading,
        quiz = quiz,
        sentences = sentences,
        isListening = isListening,
        recognizedText = recognizedText,
        isQuizCompleted = isQuizCompleted,
        insufficientDataMessage = if (hasInsufficientData) "이 문단에는 문장이 없거나 데이터를 불러올 수 없습니다." else null,
        showKoreanTranslation = showKoreanTranslation,
        availableLevels = emptyList() // 단일 문단이므로 레벨 선택 불가
    )

    val callbacks = ParagraphQuizCallbacks(
        onLevelSelected = { /* 단일 문단에서는 레벨 변경 불가 */ },
        onStartListening = { myParagraphQuizViewModel.startListening() },
        onStopListening = { myParagraphQuizViewModel.stopListening() },
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
        onResetQuiz = { 
            // 퀴즈를 리셋하고 같은 문단으로 다시 로드
            myParagraphQuizViewModel.resetQuiz()
            myParagraphQuizViewModel.clearRecognizedText()
        },
        onShowAnswers = {
            myParagraphQuizViewModel.showAllAnswers()
            myParagraphQuizViewModel.clearRecognizedText()
        },
        onToggleKoreanTranslation = { showKoreanTranslation = !showKoreanTranslation },
        onRefresh = { onBack() } // 새로고침 대신 뒤로가기
    )

    ParagraphQuizLayout(
        title = "단일 문단 퀴즈",
        state = state,
        callbacks = callbacks,
        onBack = onBack,
        refreshButtonText = "문단 상세로 돌아가기"
    )
} 