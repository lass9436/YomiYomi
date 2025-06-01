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
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuizType
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
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
    val context = LocalContext.current
    
    // 단순한 TTS만 사용
    val speechManager = remember {
        SpeechManager(context)
    }
    
    val quizState = kanjiQuizViewModel.quizState.collectAsState()
    val isLoading = kanjiQuizViewModel.isLoading.collectAsState()
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(KanjiQuizType.KANJI_TO_READING_MEANING) }
    var isLearningMode by remember { mutableStateOf(false) }

    // TTS 상태만 사용
    val isSpeaking by speechManager.isSpeaking.collectAsState()

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
        searchUrl = "https://ja.dict.naver.com/#/search?range=kanji&query="
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
        onBack = onBack,
        extraContent = {
            // 간단한 TTS 버튼만 추가
            quizState.value?.question?.let { question ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextToSpeechButton(
                        text = question,
                        isSpeaking = isSpeaking,
                        onSpeak = { speechManager.speak(it) },
                        onStop = { speechManager.stopSpeaking() },
                        size = 40.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "발음 듣기",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    )
}

@Composable
fun KanjiQuizScreenPreview() {
    KanjiQuizScreen(
        onBack = {},
        kanjiQuizViewModel = DummyKanjiQuizViewModel()
    )
}