package com.lass.yomiyomi.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lass.yomiyomi.di.SpeechManagerEntryPoint
import com.lass.yomiyomi.speech.SpeechManager
import dagger.hilt.android.EntryPointAccessors

/**
 * Composable에서 SpeechManager를 쉽게 주입받기 위한 유틸리티 함수
 */
@Composable
fun rememberSpeechManager(): SpeechManager {
    val context = LocalContext.current
    return remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
} 