package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType

data class SentenceQuizState(
    val selectedLevel: Level = Level.ALL,
    val quizTypes: List<String>,
    val selectedQuizTypeIndex: Int = 0,
    val isLearningMode: Boolean = false,
    val isLoading: Boolean = false,
    val question: String? = null,
    val showDialog: Boolean = false,
    val answerResult: String? = null,
    val searchUrl: String,
    val availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL),
    val insufficientDataMessage: String? = null,
    // 음성 인식 관련 상태
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val isAnswerCorrect: Boolean? = null
)

data class SentenceQuizCallbacks(
    val onLevelSelected: (Level) -> Unit,
    val onQuizTypeSelected: (Int) -> Unit,
    val onLearningModeChanged: (Boolean) -> Unit,
    val onStartListening: () -> Unit,
    val onStopListening: () -> Unit,
    val onCheckAnswer: (String) -> Unit,
    val onRefresh: () -> Unit,
    val onDismissDialog: () -> Unit
) 