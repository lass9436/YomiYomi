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
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomRandomViewModel
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModel
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var kanjiRepository: KanjiRepository

    @Inject
    lateinit var wordRepository: WordRepository

    // Hilt가 자동으로 의존성 주입
    private val kanjiRandomViewModel: KanjiRandomRandomViewModel by viewModels()
    private val kanjiQuizViewModel: KanjiQuizViewModel by viewModels()
    private val wordRandomViewModel: WordRandomViewModel by viewModels()
    private val wordQuizViewModel: WordQuizViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            kanjiRepository.importKanjiData(this@MainActivity)
            wordRepository.importWordData(this@MainActivity)
        }

        setContent {
            YomiYomiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(
                        kanjiRandomViewModel = kanjiRandomViewModel,
                        kanjiQuizViewModel = kanjiQuizViewModel,
                        wordRandomViewModel = wordRandomViewModel,
                        wordQuizViewModel = wordQuizViewModel,
                    )
                }
            }
        }
    }
}
