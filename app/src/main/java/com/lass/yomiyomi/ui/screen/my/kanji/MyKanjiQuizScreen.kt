package com.lass.yomiyomi.ui.screen.my.kanji

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import com.lass.yomiyomi.ui.layout.QuizLayout
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.ui.state.QuizCallbacks
import com.lass.yomiyomi.viewmodel.myKanjiQuiz.MyKanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.myKanjiQuiz.MyKanjiQuizViewModelInterface

@Composable
fun MyKanjiQuizScreen(
    onBack: () -> Unit,
    myKanjiQuizViewModel: MyKanjiQuizViewModelInterface = hiltViewModel<MyKanjiQuizViewModel>()
) {
    val quizState = myKanjiQuizViewModel.quizState.collectAsState()
    val isLoading = myKanjiQuizViewModel.isLoading.collectAsState()
    val hasInsufficientData = myKanjiQuizViewModel.hasInsufficientData.collectAsState()

    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(KanjiQuizType.KANJI_TO_READING_MEANING) }
    var isLearningMode by remember { mutableStateOf(false) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    LaunchedEffect(levelSelected, quizTypeSelected, isLearningMode) {
        myKanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
    }

    val quizTypes = listOf("한자→읽기", "읽기→한자")
    val selectedQuizTypeIndex = if (quizTypeSelected == KanjiQuizType.KANJI_TO_READING_MEANING) 0 else 1

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
                "내 한자가 없습니다.\n+ 버튼을 눌러 한자를 추가해보세요!"
            else 
                "${levelSelected.value} 레벨의 내 한자가 없습니다."
        } else null
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
                answerResult = "정답입니다!"
            } else {
                val correct = quizState.value!!
                answerResult = "오답입니다!\n정답: ${correct.answer}"
            }
            showDialog = true
            myKanjiQuizViewModel.checkAnswer(
                if (isCorrect) quizState.value!!.correctIndex else -1,
                isLearningMode
            )
        },
        onRefresh = {
            myKanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        },
        onDismissDialog = {
            showDialog = false
            answerResult = null
            myKanjiQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected, isLearningMode)
        }
    )

    QuizLayout(
        title = "내 한자 퀴즈",
        state = state,
        callbacks = callbacks,
        onBack = onBack
    )
}
