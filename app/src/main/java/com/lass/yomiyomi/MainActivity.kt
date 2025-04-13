package com.lass.yomiyomi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.ui.screen.KanjiScreen
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.KanjiViewModel
import com.lass.yomiyomi.viewmodel.KanjiViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // KanjiRepository 초기화
        val kanjiRepository = KanjiRepository(applicationContext)

        // 초기화: Kanji 데이터베이스 초기 데이터 삽입
        lifecycleScope.launch {
            kanjiRepository.initializeDatabase()
        }

        // KanjiViewModel 초기화
        val viewModelFactory = KanjiViewModelFactory(kanjiRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(KanjiViewModel::class.java)

        // UI 설정
        setContent {
            YomiYomiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    KanjiScreen(kanjiViewModel = viewModel) // ViewModel을 넘김
                }
            }
        }
    }
}