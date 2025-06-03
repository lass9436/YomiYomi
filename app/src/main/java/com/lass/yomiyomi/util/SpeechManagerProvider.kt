package com.lass.yomiyomi.util

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
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

/**
 * 🚀 Navigation-Level TTS 관리자
 * NavController의 destination 변화를 감지하여 TTS 자동 정지
 * 모든 스크린에서 speechManager 의존성 제거 가능!
 */
@Composable
fun NavigationTTSManager(navController: NavController) {
    val context = LocalContext.current
    val speechManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
    
    // 🎯 핵심: Navigation destination 변화 감지하여 TTS 정지
    // 뒤로가기, 탭 전환, 새 화면 이동 모두 감지!
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            speechManager.stopSpeaking() // 🔥 무조건 즉시!
        }
    }
}

/**
 * 전역 TTS BackHandler (deprecated - NavigationTTSManager 사용 권장)
 */
@Composable
fun GlobalTTSBackHandler() {
    val context = LocalContext.current
    val speechManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
    
    val isSpeaking = speechManager.isSpeaking.collectAsState()
    
    BackHandler(enabled = isSpeaking.value) {
        speechManager.stopSpeaking()
    }
}

/**
 * TTS를 고려한 뒤로가기 함수 (스크린에서 직접 사용하는 경우)
 */
fun SpeechManager.handleBackNavigation(onBack: () -> Unit) {
    stopSpeaking()
    onBack()
} 