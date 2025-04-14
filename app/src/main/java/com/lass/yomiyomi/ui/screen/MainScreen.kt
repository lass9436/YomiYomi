package com.lass.yomiyomi.ui.screen

import androidx.compose.runtime.*
import com.lass.yomiyomi.viewmodel.KanjiQuizViewModelInterface
import com.lass.yomiyomi.viewmodel.KanjiViewModelInterface

// MainScreen의 화면 상태 관리용 Enum
enum class Screen {
    Main,
    RandomKanji,
    KanjiQuiz
}

@Composable
fun MainScreen(kanjiViewModel: KanjiViewModelInterface, kanjiQuizViewModel: KanjiQuizViewModelInterface) {
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    when (currentScreen) {
        Screen.Main -> MainMenuScreen(
            onNavigateToKanji = { currentScreen = Screen.RandomKanji },
            onNavigateToQuiz = { currentScreen = Screen.KanjiQuiz }
        )
        Screen.RandomKanji -> KanjiScreen(
            kanjiViewModel = kanjiViewModel,
            onBack = { currentScreen = Screen.Main }
        )
        Screen.KanjiQuiz -> KanjiQuizScreen(
            kanjiQuizViewModel = kanjiQuizViewModel,
            onBack = { currentScreen = Screen.Main }
        )
    }
}