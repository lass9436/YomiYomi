package com.lass.yomiyomi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4A7B4E),        // 더 어두운 라임 그린
    secondary = Color(0xFF3A5F3D),      // 더 어두운 보조색
    tertiary = Color(0xFF81C784),       // 강조색은 그대로 유지
    background = Color(0xFF1A1C19),     // 어두운 배경에 약간의 초록빛
    surface = Color(0xFF1A1C19),        // 배경과 동일
    surfaceVariant = Color(0xFF2A3D2B), // 어두운 라임 그린 계열
    onPrimary = Color.White,            // 주요 색상 위의 텍스트
    onSecondary = Color.White,          // 보조 색상 위의 텍스트
    onTertiary = Color.Black,           // 강조색 위의 텍스트
    onBackground = Color(0xFFE3E3DC),   // 배경 위의 텍스트
    onSurface = Color(0xFFE3E3DC),      // surface 위의 텍스트
    onSurfaceVariant = Color(0xFFE3E3DC), // surfaceVariant 위의 텍스트
    primaryContainer = Color(0xFF1E3A22), // 어두운 테마용 탭 배경 (더 진하게)
    onPrimaryContainer = Color.White // 흰색 텍스트로 대비 강화
)

private val LightColorScheme = lightColorScheme(
    primary = LimeGreen,                // 기존 라임 그린
    secondary = LimeGreenLight,         // 기존 라이트 라임
    tertiary = LimeAccent,             // 기존 강조색
    background = SoftLimeBackground,    // 기존 배경색
    surface = SoftLimeBackground,       // 배경과 동일
    surfaceVariant = Color(0xFFE8F5E8), // 연한 라임 그린 계열
    onPrimary = Color.Black,           // 주요 색상 위의 텍스트
    onSecondary = Color.Black,         // 보조 색상 위의 텍스트
    onTertiary = Color.Black,          // 강조색 위의 텍스트
    onBackground = Color(0xFF1A1C19),  // 배경 위의 텍스트
    onSurface = Color(0xFF1A1C19),     // surface 위의 텍스트
    onSurfaceVariant = Color(0xFF1A1C19), // surfaceVariant 위의 텍스트
    primaryContainer = Color(0xFF4A7B4E), // 밝은 테마용 탭 배경 (더 진한 녹색)
    onPrimaryContainer = Color.White // 흰색 텍스트로 대비 강화
)

@Composable
fun YomiYomiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // 커스텀 색상을 사용하기 위해 기본값을 false로 변경
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}