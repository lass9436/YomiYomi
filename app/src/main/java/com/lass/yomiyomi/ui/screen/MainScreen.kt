package com.lass.yomiyomi.ui.screen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// 네비게이션 경로를 Enum으로 정의
enum class Routes(val route: String) {
    MAIN("main"),
    KANJI_RANDOM("kanjiRandom"),
    KANJI_QUIZ("kanjiQuiz"),
    WORD_QUIZ("wordQuiz"),
    WORD_RANDOM("wordRandom"),
    MY_WORD("myWord"),
    MY_KANJI("myKanji"),
}

@Composable
fun MainScreen(
    contentPadding: PaddingValues,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = Routes.MAIN.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Routes.MAIN.route) {
            MainMenuScreen(
                onNavigateToKanji = { navController.navigate(Routes.KANJI_RANDOM.route) },
                onNavigateToQuiz = { navController.navigate(Routes.KANJI_QUIZ.route) },
                onNavigateToWordQuiz = { navController.navigate(Routes.WORD_QUIZ.route) },
                onNavigateToWordRandom = { navController.navigate(Routes.WORD_RANDOM.route) },
                onNavigateToMyWord = { navController.navigate(Routes.MY_WORD.route) },
                onNavigateToMyKanji = { navController.navigate(Routes.MY_KANJI.route) },
            )
        }
        composable(Routes.KANJI_RANDOM.route) {
            KanjiRandomScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.KANJI_QUIZ.route) {
            KanjiQuizScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.WORD_QUIZ.route) {
            WordQuizScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.WORD_RANDOM.route) {
            WordRandomScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_WORD.route) {
            MyWordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_KANJI.route) {
            MyKanjiScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
