package com.lass.yomiyomi.ui.screen.basic.kanji

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuizType
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.viewmodel.kanjiQuiz.DummyKanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModelInterface

@Composable
fun KanjiQuizScreen(
    onBack: () -> Unit,
    kanjiQuizViewModel: KanjiQuizViewModelInterface = hiltViewModel<KanjiQuizViewModel>()
) {
    val quizState = kanjiQuizViewModel.quizState.collectAsState()
    val isLoading = kanjiQuizViewModel.isLoading.collectAsState()
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(KanjiQuizType.KANJI_TO_READING_MEANING) }
    var isLearningMode by remember { mutableStateOf(false) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    LaunchedEffect(levelSelected, quizTypeSelected, isLearningMode) {
        kanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
    }

    val quizTypes = listOf("한자→읽기", "읽기→한자")
    val selectedQuizTypeIndex = if (quizTypeSelected == KanjiQuizType.KANJI_TO_READING_MEANING) 0 else 1

    val state = QuizState(
        selectedLevel = levelSelected,
        quizTypes = quizTypes,
        selectedQuizTypeIndex = selectedQuizTypeIndex,
        isLearningMode = isLearningMode,
        isLoading = isLoading.value,
        question = quizState.value?.question,
        options = quizState.value?.options ?: emptyList(),
        showDialog = showDialog,
        answerResult = answerResult,
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query="
    )

    val callbacks = QuizCallbacks(
        onLevelSelected = { levelSelected = it },
        onQuizTypeSelected = { index ->
            quizTypeSelected = if (index == 0) {
                KanjiQuizType.KANJI_TO_READING_MEANING
            } else {
                KanjiQuizType.READING_MEANING_TO_KANJI
            }
        },
        onLearningModeChanged = { isLearningMode = it },
        onOptionSelected = { index ->
            val isCorrect = index == quizState.value?.correctIndex
            if (isCorrect) {
                answerResult = "정답입니다! 🎉"
            } else {
                val correct = quizState.value!!
                answerResult = "오답입니다! 😅\n정답: ${correct.answer}"
            }
            showDialog = true
            kanjiQuizViewModel.checkAnswer(
                if (isCorrect) quizState.value!!.correctIndex else -1,
                isLearningMode
            )
        },
        onRefresh = {
            kanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            kanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        }
    )

    QuizLayout(
        title = "한자 퀴즈 🎌",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
}

@Composable
fun KanjiQuizScreenPreview() {
    KanjiQuizScreen(
        onBack = {},
        kanjiQuizViewModel = DummyKanjiQuizViewModel()
    )
}
