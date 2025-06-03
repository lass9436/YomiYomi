package com.lass.yomiyomi.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lass.yomiyomi.di.SpeechManagerEntryPoint
import com.lass.yomiyomi.speech.SpeechManager
import dagger.hilt.android.EntryPointAccessors

/**
 * Composable에서 SpeechManager를 쉽게 주입받기 위한 유틸리티 함수
 * 화면이 사라질 때 자동으로 TTS를 정지하여 UX 개선
 */
@Composable
fun rememberSpeechManager(): SpeechManager {
    val context = LocalContext.current
    val speechManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
    
    // 화면이 사라질 때 TTS 자동 정지
    DisposableEffect(speechManager) {
        onDispose {
            speechManager.stopSpeaking()
        }
    }
    
    return speechManager
} 