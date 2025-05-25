package com.lass.yomiyomi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.ui.screen.MainScreen
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModelFactory
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomRandomViewModel
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomViewModelFactory
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModel
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModelFactory
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModel
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // KanjiRepository 초기화
        val kanjiRepository = KanjiRepository(applicationContext)
        val wordRepository = WordRepository(applicationContext)

        // 초기화: Kanji 데이터베이스 초기 데이터 삽입
        lifecycleScope.launch {
            kanjiRepository.initializeDatabase()
            wordRepository.initializeDatabase()
        }

        // KanjiViewModel 생성
        val kanjiviewModel: KanjiRandomRandomViewModel by viewModels {
            KanjiRandomViewModelFactory(kanjiRepository)
        }

        // KanjiQuizViewModel 생성
        val kanjiQuizViewModel: KanjiQuizViewModel by viewModels {
            KanjiQuizViewModelFactory(kanjiRepository)
        }

        val wordRandomViewModel: WordRandomViewModel by viewModels {
            WordRandomViewModelFactory(wordRepository)
        }

        val wordQuizViewModel: WordQuizViewModel by viewModels {
            WordQuizViewModelFactory(wordRepository)
        }

        // UI 설정
        setContent {
            YomiYomiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        kanjiviewModel,
                        kanjiQuizViewModel,
                        wordRandomViewModel,
                        wordQuizViewModel
                    )
                }
            }
        }
    }
}