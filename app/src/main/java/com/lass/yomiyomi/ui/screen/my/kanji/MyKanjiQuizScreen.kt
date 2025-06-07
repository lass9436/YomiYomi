package com.lass.yomiyomi.ui.screen.my.kanji

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.viewmodel.myKanji.quiz.DummyMyKanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.myKanji.quiz.MyKanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.myKanji.quiz.MyKanjiQuizViewModelInterface

@Composable
fun MyKanjiQuizScreen(
    onBack: () -> Unit,
    myKanjiQuizViewModel: MyKanjiQuizViewModelInterface = hiltViewModel<MyKanjiQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // ViewModel state 수집
    val quizData by myKanjiQuizViewModel.quizState.collectAsState()
    val isLoading by myKanjiQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData by myKanjiQuizViewModel.hasInsufficientData.collectAsState()

    // UI state 관리
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var selectedQuizTypeIndex by remember { mutableStateOf(0) }
    var isLearningMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var answerResult by remember { mutableStateOf<String?>(null) }

    val quizTypes = listOf("한자 → 읽기/뜻", "읽기/뜻 → 한자")
    val kanjiQuizTypes = listOf(KanjiQuizType.KANJI_TO_READING_MEANING, KanjiQuizType.READING_MEANING_TO_KANJI)

    // Quiz state 생성
    val state = QuizState(
        selectedLevel = selectedLevel,
        quizTypes = quizTypes,
        selectedQuizTypeIndex = selectedQuizTypeIndex,
        isLearningMode = isLearningMode,
        isLoading = isLoading,
        question = quizData?.question,
        options = quizData?.options ?: emptyList(),
        showDialog = showDialog,
        answerResult = answerResult,
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        insufficientDataMessage = if (hasInsufficientData) "내 한자 데이터가 부족합니다.\n한자를 더 추가해주세요." else null
    )

    // Callbacks 생성
    val callbacks = QuizCallbacks(
        onLevelSelected = { level ->
            selectedLevel = level
            myKanjiQuizViewModel.loadQuizByLevel(level, kanjiQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onQuizTypeSelected = { index ->
            selectedQuizTypeIndex = index
            myKanjiQuizViewModel.loadQuizByLevel(selectedLevel, kanjiQuizTypes[index], isLearningMode)
        },
        onLearningModeChanged = { learningMode ->
            isLearningMode = learningMode
            myKanjiQuizViewModel.loadQuizByLevel(selectedLevel, kanjiQuizTypes[selectedQuizTypeIndex], learningMode)
        },
        onOptionSelected = { selectedIndex ->
            myKanjiQuizViewModel.checkAnswer(selectedIndex, isLearningMode)
            val isCorrect = selectedIndex == (quizData?.correctIndex ?: -1)
            answerResult = if (isCorrect) {
                "정답입니다! 🎉\n정답: ${quizData?.answer ?: ""}"
            } else {
                "틀렸습니다. 😅\n정답: ${quizData?.answer ?: ""}"
            }
            showDialog = true
        },
        onRefresh = {
            myKanjiQuizViewModel.loadQuizByLevel(selectedLevel, kanjiQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            myKanjiQuizViewModel.loadQuizByLevel(selectedLevel, kanjiQuizTypes[selectedQuizTypeIndex], isLearningMode)
        }
    )

    // 초기 퀴즈 로드
    LaunchedEffect(Unit) {
        myKanjiQuizViewModel.loadQuizByLevel(selectedLevel, kanjiQuizTypes[selectedQuizTypeIndex], isLearningMode)
    }

    QuizLayout(
        title = "내 한자 퀴즈 🎌",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
}

@Composable
fun MyKanjiQuizScreenPreview() {
    MyKanjiQuizScreen(
        onBack = {},
        myKanjiQuizViewModel = DummyMyKanjiQuizViewModel()
    )
}
