package com.lass.yomiyomi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.data.repository.MyWordRepository
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

    @Inject
    lateinit var myKanjiRepository: MyKanjiRepository

    @Inject
    lateinit var myWordRepository: MyWordRepository

    // 음성 인식 권한 요청
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // 권한이 거부된 경우 사용자에게 알림 (필요 시)
            // Toast나 Dialog로 권한이 필요하다는 것을 알릴 수 있음
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 음성 인식 권한 확인 및 요청
        checkAndRequestAudioPermission()

        // 앱 시작시 데이터 초기화만 담당
        lifecycleScope.launch {
            kanjiRepository.importKanjiData(this@MainActivity)
            wordRepository.importWordData(this@MainActivity)
            myParagraphRepository.importParagraphData(this@MainActivity)
            mySentenceRepository.importSentenceData(this@MainActivity)
            myKanjiRepository.importMyKanjiData(this@MainActivity)
            myWordRepository.importMyWordData(this@MainActivity)
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

    private fun checkAndRequestAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용된 경우
            }
            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
