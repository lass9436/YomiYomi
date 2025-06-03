package com.lass.yomiyomi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.ui.screen.main.MainScreen
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var kanjiRepository: KanjiRepository

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var mySentenceRepository: MySentenceRepository

    @Inject
    lateinit var myParagraphRepository: MyParagraphRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 앱 시작시 데이터 초기화만 담당
        lifecycleScope.launch {
            kanjiRepository.importKanjiData(this@MainActivity)
            wordRepository.importWordData(this@MainActivity)
            myParagraphRepository.importParagraphData(this@MainActivity)
            mySentenceRepository.importSentenceData(this@MainActivity)
        }

        setContent {
            YomiYomiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { contentPadding ->
                    MainScreen(
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}
