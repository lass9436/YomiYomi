package com.lass.yomiyomi.ui.screen

import androidx.compose.runtime.*
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModelInterface
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomViewModelInterface
import androidx.activity.compose.BackHandler
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModelInterface
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModelInterface

// MainScreen의 화면 상태 관리용 Enum
enum class Screen {
    Main,
    RandomKanji,
    KanjiQuiz,
    WordQuiz,
    WordRandom
}

@Composable
fun MainScreen(
    kanjiViewModel: KanjiRandomViewModelInterface,
    kanjiQuizViewModel: KanjiQuizViewModelInterface,
    wordRandomViewModel: WordRandomViewModelInterface,
    wordQuizViewModel: WordQuizViewModelInterface,
) {
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    // 뒤로가기에 대한 처리
    BackHandler(enabled = currentScreen != Screen.Main) {
        currentScreen = Screen.Main
    }

    when (currentScreen) {
        Screen.Main -> MainMenuScreen(
            onNavigateToKanji = { currentScreen = Screen.RandomKanji },
            onNavigateToQuiz = { currentScreen = Screen.KanjiQuiz },
            onNavigateToWordQuiz = { currentScreen = Screen.WordQuiz },
            onNavigateToWordRandom = { currentScreen = Screen.WordRandom }
        )

        Screen.RandomKanji -> KanjiScreen(
            kanjiViewModel = kanjiViewModel,
            onBack = { currentScreen = Screen.Main }
        )
        Screen.KanjiQuiz -> KanjiQuizScreen(
            kanjiQuizViewModel = kanjiQuizViewModel,
            onBack = { currentScreen = Screen.Main }
        )
        Screen.WordQuiz -> WordQuizScreen(
            wordQuizViewModel = wordQuizViewModel,
            onBack = { currentScreen = Screen.Main }
        )
        Screen.WordRandom -> WordRandomScreen(
            wordViewModel = wordRandomViewModel,
            onBack = { currentScreen = Screen.Main }
        )
    }
}