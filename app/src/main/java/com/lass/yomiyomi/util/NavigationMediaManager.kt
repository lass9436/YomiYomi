package com.lass.yomiyomi.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.lass.yomiyomi.di.MediaManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * 🚀 Navigation-Level TTS 관리자
 * NavController의 destination 변화를 감지하여 TTS 자동 정지
 * 모든 스크린에서 speechManager 의존성 제거 가능!
 */
@Composable
fun NavigationMediaManager(navController: NavController) {
    val context = LocalContext.current
    val mediaManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            MediaManagerEntryPoint::class.java
        ).mediaManager()
    }
    
    // 🎯 핵심: Navigation destination 변화 감지하여 TTS 정지
    // 뒤로가기, 탭 전환, 새 화면 이동 모두 감지!
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            mediaManager.stopForegroundAndRecognition() // 포그라운드 TTS, 음성인식(녹음)만 중지. 백그라운드는 멈추지 않음
        }
    }
} 
