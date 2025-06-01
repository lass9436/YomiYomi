package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.data.model.Level

data class QuizState(
    val selectedLevel: Level = Level.ALL,
    val quizTypes: List<String>,
    val selectedQuizTypeIndex: Int = 0,
    val isLearningMode: Boolean = false,
    val isLoading: Boolean = false,
    val question: String? = null,
    val options: List<String> = emptyList(),
    val showDialog: Boolean = false,
    val answerResult: String? = null,
    val searchUrl: String,
    val availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL)
)

data class QuizCallbacks(
    val onLevelSelected: (Level) -> Unit,
    val onQuizTypeSelected: (Int) -> Unit,
    val onLearningModeChanged: (Boolean) -> Unit,
    val onOptionSelected: (Int) -> Unit,
    val onRefresh: () -> Unit,
    val onDismissDialog: () -> Unit
) 