package com.lass.yomiyomi.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

/**
 * 마이크 권한 상태
 */
enum class AudioPermissionState {
    GRANTED,
    DENIED,
    NOT_REQUESTED
}

/**
 * 마이크 권한 관리 유틸리티
 */
@Composable
fun rememberAudioPermissionState(
    onPermissionResult: (Boolean) -> Unit = {}
): Pair<AudioPermissionState, () -> Unit> {
    val permissionState = remember { mutableStateOf(AudioPermissionState.NOT_REQUESTED) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = if (isGranted) {
            AudioPermissionState.GRANTED
        } else {
            AudioPermissionState.DENIED
        }
        onPermissionResult(isGranted)
    }
    
    val requestPermission = {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
    }
    
    return permissionState.value to requestPermission
}

/**
 * 마이크 권한 체크
 */
fun Context.hasAudioPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
} 