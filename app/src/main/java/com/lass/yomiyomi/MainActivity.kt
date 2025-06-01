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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var kanjiRepository: KanjiRepository

    @Inject
    lateinit var wordRepository: WordRepository

    private val kanjiRandomViewModel: KanjiRandomRandomViewModel by viewModels {
        KanjiRandomViewModelFactory(kanjiRepository)
    }

    private val kanjiQuizViewModel: KanjiQuizViewModel by viewModels {
        KanjiQuizViewModelFactory(kanjiRepository)
    }

    private val wordRandomViewModel: WordRandomViewModel by viewModels {
        WordRandomViewModelFactory(wordRepository)
    }

    private val wordQuizViewModel: WordQuizViewModel by viewModels {
        WordQuizViewModelFactory(wordRepository)
    }

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
