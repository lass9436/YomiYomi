package com.lass.yomiyomi.ui.screen.my.word

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.viewmodel.myWord.quiz.DummyMyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.myWord.quiz.MyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.myWord.quiz.MyWordQuizViewModelInterface

@Composable
fun MyWordQuizScreen(
    onBack: () -> Unit,
    myWordQuizViewModel: MyWordQuizViewModelInterface = hiltViewModel<MyWordQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // ViewModel state 수집
    val quizData by myWordQuizViewModel.quizState.collectAsState()
    val isLoading by myWordQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData by myWordQuizViewModel.hasInsufficientData.collectAsState()

    // UI state 관리
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var selectedQuizTypeIndex by remember { mutableStateOf(0) }
    var isLearningMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var answerResult by remember { mutableStateOf<String?>(null) }

    val quizTypes = listOf("단어 → 뜻/읽기", "뜻/읽기 → 단어")
    val wordQuizTypes = listOf(WordQuizType.WORD_TO_MEANING_READING, WordQuizType.MEANING_READING_TO_WORD)

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
        searchUrl = "https://jisho.org/search/",
        insufficientDataMessage = if (hasInsufficientData) "내 단어 데이터가 부족합니다.\n단어를 더 추가해주세요." else null
    )

    // Callbacks 생성
    val callbacks = QuizCallbacks(
        onLevelSelected = { level ->
            selectedLevel = level
            myWordQuizViewModel.loadQuizByLevel(level, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onQuizTypeSelected = { index ->
            selectedQuizTypeIndex = index
            myWordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[index], isLearningMode)
        },
        onLearningModeChanged = { learningMode ->
            isLearningMode = learningMode
            myWordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], learningMode)
        },
        onOptionSelected = { selectedIndex ->
            myWordQuizViewModel.checkAnswer(selectedIndex, isLearningMode)
            val isCorrect = selectedIndex == (quizData?.correctIndex ?: -1)
            answerResult = if (isCorrect) "정답입니다! 🎉" else "틀렸습니다. 정답: ${quizData?.answer ?: ""}"
            showDialog = true
        },
        onRefresh = {
            myWordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            myWordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        }
    )

    // 초기 퀴즈 로드
    LaunchedEffect(Unit) {
        myWordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
    }

    QuizLayout(
        title = "내 단어 퀴즈 📝",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
}

@Composable
fun MyWordQuizScreenPreview() {
    MyWordQuizScreen(
        onBack = {},
        myWordQuizViewModel = DummyMyWordQuizViewModel()
    )
} 
