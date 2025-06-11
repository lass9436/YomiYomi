package com.lass.yomiyomi.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lass.yomiyomi.di.SpeechManagerEntryPoint
import com.lass.yomiyomi.tts.ForegroundTTSManager
import com.lass.yomiyomi.speech.SpeechRecognitionManager
import dagger.hilt.android.EntryPointAccessors

/**
 * Composable에서 SpeechManager를 쉽게 주입받기 위한 유틸리티 함수
 * NavigationTTSManager가 화면 전환 시 TTS 정지를 담당
 */
@Composable
fun rememberSpeechManager(): ForegroundTTSManager {
    val context = LocalContext.current
    val speechManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
    
    return speechManager
}

@Composable
fun rememberSpeechRecognitionManager(): SpeechRecognitionManager {
    val context = LocalContext.current
    val speechRecognitionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechRecognitionManager()
    }
    return speechRecognitionManager
} 
