package com.lass.yomiyomi.ui.screen.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lass.yomiyomi.ui.screen.basic.kanji.KanjiListScreen
import com.lass.yomiyomi.ui.screen.basic.kanji.KanjiQuizScreen
import com.lass.yomiyomi.ui.screen.basic.kanji.KanjiRandomScreen
import com.lass.yomiyomi.ui.screen.basic.word.WordListScreen
import com.lass.yomiyomi.ui.screen.basic.word.WordQuizScreen
import com.lass.yomiyomi.ui.screen.basic.word.WordRandomScreen
import com.lass.yomiyomi.ui.screen.my.kanji.MyKanjiQuizScreen
import com.lass.yomiyomi.ui.screen.my.kanji.MyKanjiRandomScreen
import com.lass.yomiyomi.ui.screen.my.kanji.MyKanjiScreen
import com.lass.yomiyomi.ui.screen.my.paragraph.ParagraphListScreen
import com.lass.yomiyomi.ui.screen.my.sentence.SentenceListScreen
import com.lass.yomiyomi.ui.screen.my.sentence.MySentenceRandomScreen
import com.lass.yomiyomi.ui.screen.my.sentence.MySentenceQuizScreen
import com.lass.yomiyomi.ui.screen.my.sentence.SingleSentenceQuizScreen
import com.lass.yomiyomi.ui.screen.my.word.MyWordQuizScreen
import com.lass.yomiyomi.ui.screen.my.word.MyWordRandomScreen
import com.lass.yomiyomi.ui.screen.my.word.MyWordScreen
import com.lass.yomiyomi.ui.screen.my.paragraph.ParagraphDetailScreen
import com.lass.yomiyomi.ui.screen.my.paragraph.MyParagraphRandomScreen
import com.lass.yomiyomi.ui.screen.my.paragraph.MyParagraphQuizScreen
import com.lass.yomiyomi.ui.screen.my.paragraph.SingleParagraphQuizScreen
import com.lass.yomiyomi.util.NavigationMediaManager

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
    MY_SENTENCE_LIST("mySentenceList"),
    MY_SENTENCE_RANDOM("mySentenceRandom"),
    MY_SENTENCE_QUIZ("mySentenceQuiz"),
    SINGLE_SENTENCE_QUIZ("singleSentenceQuiz/{sentenceId}"),
    MY_PARAGRAPH_LIST("myParagraphList"),
    MY_PARAGRAPH_RANDOM("myParagraphRandom"),
    MY_PARAGRAPH_QUIZ("myParagraphQuiz"),
    SINGLE_PARAGRAPH_QUIZ("singleParagraphQuiz/{paragraphId}"),
    MY_PARAGRAPH_DETAIL("myParagraphDetail/{paragraphId}")
}

@Composable
fun MainScreen(
    contentPadding: PaddingValues,
) {
    val navController = rememberNavController()
    
    // 🚀 Navigation-Level TTS 관리 - 모든 화면 전환 시 TTS 자동 정지
    NavigationMediaManager(navController)

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
                onNavigateToSentenceList = { navController.navigate(Routes.MY_SENTENCE_LIST.route) },
                onNavigateToSentenceRandom = { navController.navigate(Routes.MY_SENTENCE_RANDOM.route) },
                onNavigateToSentenceQuiz = { navController.navigate(Routes.MY_SENTENCE_QUIZ.route) },
                onNavigateToParagraphList = { navController.navigate(Routes.MY_PARAGRAPH_LIST.route) },
                onNavigateToParagraphRandom = { navController.navigate(Routes.MY_PARAGRAPH_RANDOM.route) },
                onNavigateToParagraphQuiz = { navController.navigate(Routes.MY_PARAGRAPH_QUIZ.route) }
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
                onNavigateToSentenceList = { navController.navigate(Routes.MY_SENTENCE_LIST.route) },
                onNavigateToSentenceRandom = { navController.navigate(Routes.MY_SENTENCE_RANDOM.route) },
                onNavigateToSentenceQuiz = { navController.navigate(Routes.MY_SENTENCE_QUIZ.route) },
                onNavigateToParagraphList = { navController.navigate(Routes.MY_PARAGRAPH_LIST.route) },
                onNavigateToParagraphRandom = { navController.navigate(Routes.MY_PARAGRAPH_RANDOM.route) },
                onNavigateToParagraphQuiz = { navController.navigate(Routes.MY_PARAGRAPH_QUIZ.route) }
            )
        }
        composable(Routes.KANJI_LIST.route) {
            KanjiListScreen(
                onNavigateBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.WORD_LIST.route) {
            WordListScreen(
                onNavigateBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.KANJI_RANDOM.route) {
            KanjiRandomScreen(
                onBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.KANJI_QUIZ.route) {
            KanjiQuizScreen(
                onBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.WORD_QUIZ.route) {
            WordQuizScreen(
                onBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.WORD_RANDOM.route) {
            WordRandomScreen(
                onBack = {
                    navController.navigate("main/0") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_WORD.route) {
            MyWordScreen(
                onNavigateBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_KANJI.route) {
            MyKanjiScreen(
                onNavigateBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_WORD_RANDOM.route) {
            MyWordRandomScreen(
                onBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_KANJI_RANDOM.route) {
            MyKanjiRandomScreen(
                onBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_KANJI_QUIZ.route) {
            MyKanjiQuizScreen(
                onBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_WORD_QUIZ.route) {
            MyWordQuizScreen(
                onBack = {
                    navController.navigate("main/1") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_SENTENCE_LIST.route) {
            SentenceListScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                },
                onSentenceQuiz = { sentence ->
                    navController.navigate("singleSentenceQuiz/${sentence.id}")
                }
            )
        }
        composable(Routes.MY_SENTENCE_RANDOM.route) {
            MySentenceRandomScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MY_SENTENCE_QUIZ.route) {
            MySentenceQuizScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.SINGLE_SENTENCE_QUIZ.route,
            arguments = listOf(navArgument("sentenceId") { 
                type = NavType.IntType 
            })
        ) { backStackEntry ->
            val sentenceId = backStackEntry.arguments?.getInt("sentenceId") ?: 0
            SingleSentenceQuizScreen(
                sentenceId = sentenceId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_PARAGRAPH_LIST.route) {
            ParagraphListScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                },
                onParagraphClick = { paragraph ->
                    navController.navigate("myParagraphDetail/${paragraph.paragraphId}")
                }
            )
        }
        composable(Routes.MY_PARAGRAPH_RANDOM.route) {
            MyParagraphRandomScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                },
                onParagraphClick = { paragraphId ->
                    navController.navigate("myParagraphDetail/${paragraphId}")
                }
            )
        }
        composable(Routes.MY_PARAGRAPH_QUIZ.route) {
            MyParagraphQuizScreen(
                onBack = {
                    navController.navigate("main/2") {
                        popUpTo(Routes.MAIN.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.SINGLE_PARAGRAPH_QUIZ.route,
            arguments = listOf(navArgument("paragraphId") { 
                type = NavType.IntType 
            })
        ) { backStackEntry ->
            val paragraphId = backStackEntry.arguments?.getInt("paragraphId") ?: 0
            SingleParagraphQuizScreen(
                paragraphId = paragraphId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.MY_PARAGRAPH_DETAIL.route,
            arguments = listOf(navArgument("paragraphId") { 
                type = NavType.IntType 
            })
        ) { backStackEntry ->
            val paragraphId = backStackEntry.arguments?.getInt("paragraphId") ?: 0
            ParagraphDetailScreen(
                paragraphId = paragraphId,
                onBack = { navController.popBackStack() },
                onQuiz = { navController.navigate("singleParagraphQuiz/${paragraphId}") }
            )
        }
    }
}
