package com.lass.yomiyomi.ui.screen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// 네비게이션 경로를 Enum으로 정의
enum class Routes(val route: String) {
    MAIN("main"),
    MAIN_WITH_TAB("main/{tabIndex}"),
    KANJI_LIST("kanjiList"),
    WORD_LIST("wordList"),
    KANJI_RANDOM("kanjiRandom"),
    KANJI_QUIZ("kanjiQuiz"),
    WORD_QUIZ("wordQuiz"),
    WORD_RANDOM("wordRandom"),
    MY_WORD("myWord"),
    MY_KANJI("myKanji"),
    MY_WORD_RANDOM("myWordRandom"),
    MY_KANJI_RANDOM("myKanjiRandom"),
    MY_KANJI_QUIZ("myKanjiQuiz"),
    MY_WORD_QUIZ("myWordQuiz"),
    SENTENCE_LIST("sentenceList"),
    PARAGRAPH_LIST("paragraphList"),
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
                initialTabIndex = 0,
                onNavigateToKanji = { navController.navigate(Routes.KANJI_RANDOM.route) },
                onNavigateToKanjiList = { navController.navigate(Routes.KANJI_LIST.route) },
                onNavigateToQuiz = { navController.navigate(Routes.KANJI_QUIZ.route) },
                onNavigateToWordQuiz = { navController.navigate(Routes.WORD_QUIZ.route) },
                onNavigateToWordRandom = { navController.navigate(Routes.WORD_RANDOM.route) },
                onNavigateToWordList = { navController.navigate(Routes.WORD_LIST.route) },
                onNavigateToMyWord = { navController.navigate(Routes.MY_WORD.route) },
                onNavigateToMyKanji = { navController.navigate("${Routes.MAIN_WITH_TAB.route.replace("{tabIndex}", "1")}") },
                onNavigateToMyWordRandom = { navController.navigate(Routes.MY_WORD_RANDOM.route) },
                onNavigateToMyKanjiRandom = { navController.navigate(Routes.MY_KANJI_RANDOM.route) },
                onNavigateToMyKanjiQuiz = { navController.navigate(Routes.MY_KANJI_QUIZ.route) },
                onNavigateToMyWordQuiz = { navController.navigate(Routes.MY_WORD_QUIZ.route) },
                onNavigateToSentenceList = { navController.navigate(Routes.SENTENCE_LIST.route) },
                onNavigateToParagraphList = { navController.navigate(Routes.PARAGRAPH_LIST.route) },
            )
        }
        composable(
            route = Routes.MAIN_WITH_TAB.route,
            arguments = listOf(navArgument("tabIndex") { 
                type = NavType.IntType 
                defaultValue = 0
            })
        ) { backStackEntry ->
            val tabIndex = backStackEntry.arguments?.getInt("tabIndex") ?: 0
            MainMenuScreen(
                initialTabIndex = tabIndex,
                onNavigateToKanji = { navController.navigate(Routes.KANJI_RANDOM.route) },
                onNavigateToKanjiList = { navController.navigate(Routes.KANJI_LIST.route) },
                onNavigateToQuiz = { navController.navigate(Routes.KANJI_QUIZ.route) },
                onNavigateToWordQuiz = { navController.navigate(Routes.WORD_QUIZ.route) },
                onNavigateToWordRandom = { navController.navigate(Routes.WORD_RANDOM.route) },
                onNavigateToWordList = { navController.navigate(Routes.WORD_LIST.route) },
                onNavigateToMyWord = { navController.navigate(Routes.MY_WORD.route) },
                onNavigateToMyKanji = { navController.navigate(Routes.MY_KANJI.route) },
                onNavigateToMyWordRandom = { navController.navigate(Routes.MY_WORD_RANDOM.route) },
                onNavigateToMyKanjiRandom = { navController.navigate(Routes.MY_KANJI_RANDOM.route) },
                onNavigateToMyKanjiQuiz = { navController.navigate(Routes.MY_KANJI_QUIZ.route) },
                onNavigateToMyWordQuiz = { navController.navigate(Routes.MY_WORD_QUIZ.route) },
                onNavigateToSentenceList = { navController.navigate(Routes.SENTENCE_LIST.route) },
                onNavigateToParagraphList = { navController.navigate(Routes.PARAGRAPH_LIST.route) },
            )
        }
        composable(Routes.KANJI_LIST.route) {
            KanjiListScreen(
                onNavigateBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.WORD_LIST.route) {
            WordListScreen(
                onNavigateBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.KANJI_RANDOM.route) {
            KanjiRandomScreen(
                onBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.KANJI_QUIZ.route) {
            KanjiQuizScreen(
                onBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.WORD_QUIZ.route) {
            WordQuizScreen(
                onBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.WORD_RANDOM.route) {
            WordRandomScreen(
                onBack = { navController.navigate("main/0") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_WORD.route) {
            MyWordScreen(
                onNavigateBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_KANJI.route) {
            MyKanjiScreen(
                onNavigateBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_WORD_RANDOM.route) {
            MyWordRandomScreen(
                onBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_KANJI_RANDOM.route) {
            MyKanjiRandomScreen(
                onBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_KANJI_QUIZ.route) {
            MyKanjiQuizScreen(
                onBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.MY_WORD_QUIZ.route) {
            MyWordQuizScreen(
                onBack = { navController.navigate("main/1") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.SENTENCE_LIST.route) {
            SentenceListScreen(
                onBack = { navController.navigate("main/2") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } }
            )
        }
        composable(Routes.PARAGRAPH_LIST.route) {
            ParagraphListScreen(
                onBack = { navController.navigate("main/2") {
                    popUpTo(Routes.MAIN.route) { inclusive = true }
                } },
                onParagraphClick = { paragraph ->
                    // TODO: 추후 문단 상세 화면으로 이동
                    // navController.navigate("paragraphDetail/${paragraph.paragraphId}")
                }
            )
        }
    }
}
