package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.layout.SingleSentenceQuizLayout
import com.lass.yomiyomi.ui.state.SingleSentenceQuizCallbacks
import com.lass.yomiyomi.ui.state.SingleSentenceQuizState
import com.lass.yomiyomi.viewmodel.myParagraph.quiz.MyParagraphQuizViewModel
import com.lass.yomiyomi.viewmodel.myParagraph.quiz.MyParagraphQuizViewModelInterface
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SingleSentenceQuizScreen(
    sentence: SentenceItem,
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

    // UI 상태 관리
    var showKoreanTranslation by remember { mutableStateOf(true) }

    // Quiz state 생성
    val state = SingleSentenceQuizState(
        sentence = sentence,
        quiz = quizData,
        isLoading = isLoading,
        insufficientDataMessage = if (hasInsufficientData) "퀴즈할 문장이 부족합니다." else null,
        isListening = isListening,
        recognizedText = recognizedText,
        isQuizCompleted = isQuizCompleted,
        showKoreanTranslation = showKoreanTranslation
    )

    // Quiz callbacks 생성
    val callbacks = SingleSentenceQuizCallbacks(
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
        },
        onBackToSentenceList = {
            myParagraphQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
            onBack()
        }
    )

    // 초기 퀴즈 로드 (특정 문장으로)
    LaunchedEffect(sentence) {
        // 단일 문장을 이용해 퀴즈 생성
        // ParagraphQuizGenerator를 사용하여 단일 문장으로 퀴즈 생성
        myParagraphQuizViewModel.loadQuizBySentence(sentence, ParagraphQuizType.FILL_IN_BLANKS_SPEECH)
    }

    SingleSentenceQuizLayout(
        title = "문장 퀴즈 🧩",
        state = state,
        callbacks = callbacks,
        onBack = {
            myParagraphQuizViewModel.stopListening() // 뒤로가기 시 음성 인식 중지
            onBack()
        }
    )
} 