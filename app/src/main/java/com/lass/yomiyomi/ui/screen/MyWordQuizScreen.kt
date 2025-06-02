package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.WordQuizType
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.viewmodel.myWordQuiz.MyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.myWordQuiz.MyWordQuizViewModelInterface

@Composable
fun MyWordQuizScreen(
    onBack: () -> Unit,
    myWordQuizViewModel: MyWordQuizViewModelInterface = hiltViewModel<MyWordQuizViewModel>()
) {
    val quizState = myWordQuizViewModel.quizState.collectAsState()
    val isLoading = myWordQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData = myWordQuizViewModel.hasInsufficientData.collectAsState()
    
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(WordQuizType.WORD_TO_MEANING_READING) }
    var isLearningMode by remember { mutableStateOf(false) }

    LaunchedEffect(levelSelected, quizTypeSelected, isLearningMode) {
        myWordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
    }

    val quizTypes = listOf("단어→의미", "의미→단어")
    val selectedQuizTypeIndex = if (quizTypeSelected == WordQuizType.WORD_TO_MEANING_READING) 0 else 1

    val state = QuizState(
        selectedLevel = levelSelected,
        quizTypes = quizTypes,
        selectedQuizTypeIndex = selectedQuizTypeIndex,
        isLearningMode = isLearningMode,
        isLoading = isLoading.value,
        question = if (hasInsufficientData.value) null else quizState.value?.question,
        options = if (hasInsufficientData.value) emptyList() else (quizState.value?.options ?: emptyList()),
        showDialog = showDialog,
        answerResult = answerResult,
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=",
        insufficientDataMessage = if (hasInsufficientData.value) {
            if (levelSelected == Level.ALL) 
                "내 단어가 없습니다.\n+ 버튼을 눌러 단어를 추가해보세요!"
            else 
                "${levelSelected.value} 레벨의 내 단어가 없습니다."
        } else null
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
            myWordQuizViewModel.checkAnswer(
                if (isCorrect) quizState.value!!.correctIndex else -1,
                isLearningMode
            )
        },
        onRefresh = {
            myWordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            myWordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        }
    )

    QuizLayout(
        title = "내 단어 퀴즈",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
} 
