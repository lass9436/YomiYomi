package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.WordQuizType
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.viewmodel.wordQuiz.DummyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModel
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModelInterface

@Composable
fun WordQuizScreen(
    onBack: () -> Unit,
    wordQuizViewModel: WordQuizViewModelInterface = hiltViewModel<WordQuizViewModel>()
) {
    val quizState = wordQuizViewModel.quizState.collectAsState()
    val isLoading = wordQuizViewModel.isLoading.collectAsState()
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(WordQuizType.WORD_TO_MEANING_READING) }
    var isLearningMode by remember { mutableStateOf(false) }

    LaunchedEffect(levelSelected, quizTypeSelected, isLearningMode) {
        wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
    }

    val quizTypes = listOf("단어→의미", "의미→단어")
    val selectedQuizTypeIndex = if (quizTypeSelected == WordQuizType.WORD_TO_MEANING_READING) 0 else 1

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
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL) // 단어 퀴즈에서는 N1 제외
    )

    val callbacks = QuizCallbacks(
        onLevelSelected = { levelSelected = it },
        onQuizTypeSelected = { index ->
            quizTypeSelected = if (index == 0) {
                WordQuizType.WORD_TO_MEANING_READING
            } else {
                WordQuizType.MEANING_READING_TO_WORD
            }
        },
        onLearningModeChanged = { isLearningMode = it },
        onOptionSelected = { index ->
            val isCorrect = index == quizState.value?.correctIndex
            if (isCorrect) {
                answerResult = "정답입니다!"
            } else {
                val correct = quizState.value!!
                answerResult = "오답입니다!\n정답: ${correct.answer}"
            }
            showDialog = true
            wordQuizViewModel.checkAnswer(
                if (isCorrect) quizState.value!!.correctIndex else -1,
                isLearningMode
            )
        },
        onRefresh = {
            wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        }
    )

    QuizLayout(
        title = "단어 퀴즈",
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
