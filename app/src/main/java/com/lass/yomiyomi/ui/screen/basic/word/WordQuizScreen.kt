package com.lass.yomiyomi.ui.screen.basic.word

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.viewmodel.word.quiz.DummyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.word.quiz.WordQuizViewModel
import com.lass.yomiyomi.viewmodel.word.quiz.WordQuizViewModelInterface

@Composable
fun WordQuizScreen(
    onBack: () -> Unit,
    wordQuizViewModel: WordQuizViewModelInterface = hiltViewModel<WordQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // ViewModel state 수집
    val quizData by wordQuizViewModel.quizState.collectAsState()
    val isLoading by wordQuizViewModel.isLoading.collectAsState()

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
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        insufficientDataMessage = if (!isLoading && quizData == null) "단어 데이터가 부족합니다.\n새로고침 해주세요." else null
    )

    // Callbacks 생성
    val callbacks = QuizCallbacks(
        onLevelSelected = { level ->
            selectedLevel = level
            wordQuizViewModel.loadQuizByLevel(level, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onQuizTypeSelected = { index ->
            selectedQuizTypeIndex = index
            wordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[index], isLearningMode)
        },
        onLearningModeChanged = { learningMode ->
            isLearningMode = learningMode
            wordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], learningMode)
        },
        onOptionSelected = { selectedIndex ->
            wordQuizViewModel.checkAnswer(selectedIndex, isLearningMode)
            val isCorrect = selectedIndex == (quizData?.correctIndex ?: -1)
            answerResult = if (isCorrect) {
                "정답입니다! 🎉\n정답: ${quizData?.answer ?: ""}"
            } else {
                "틀렸습니다. 😅\n정답: ${quizData?.answer ?: ""}"
            }
            showDialog = true
        },
        onRefresh = {
            wordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            wordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
        }
    )

    // 초기 퀴즈 로드
    LaunchedEffect(Unit) {
        wordQuizViewModel.loadQuizByLevel(selectedLevel, wordQuizTypes[selectedQuizTypeIndex], isLearningMode)
    }

    QuizLayout(
        title = "단어 퀴즈 📝",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
}

@Composable
fun WordQuizScreenPreview() {
    WordQuizScreen(
        onBack = {},
        wordQuizViewModel = DummyWordQuizViewModel()
    )
}
