package com.lass.yomiyomi.ui.screen

import androidx.compose.runtime.*
import com.lass.yomiyomi.viewmodel.KanjiQuizViewModelInterface
import com.lass.yomiyomi.viewmodel.KanjiViewModelInterface
import androidx.activity.compose.BackHandler

// MainScreen의 화면 상태 관리용 Enum
enum class Screen {
    Main,
    RandomKanji,
    KanjiQuiz
}

@Composable
fun MainScreen(kanjiViewModel: KanjiViewModelInterface, kanjiQuizViewModel: KanjiQuizViewModelInterface) {
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    // 뒤로가기에 대한 처리
    BackHandler(enabled = currentScreen != Screen.Main) {
        currentScreen = Screen.Main
    }

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