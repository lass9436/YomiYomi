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
import com.lass.yomiyomi.data.model.Level
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

    // 데이터 부족 상태 처리
    if (hasInsufficientData.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📖",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "내 단어 데이터가 부족합니다",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "퀴즈를 만들기 위해서는 최소 4개의 단어가 필요합니다.\n내 단어에 더 많은 단어를 추가해 주세요.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("뒤로 가기")
                }
                Button(
                    onClick = {
                        myWordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
                    }
                ) {
                    Text("다시 시도")
                }
            }
        }
        return
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
        searchUrl = "https://ja.dict.naver.com/#/search?range=word&query="
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