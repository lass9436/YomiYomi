package com.lass.yomiyomi.ui.component.text.tts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 메인 텍스트와 TTS 버튼을 함께 표시하는 컴포넌트
 * Quiz, Random 등에서 사용
 */
@Composable
fun MainTextWithTTS(
    text: String,
    fontSize: TextUnit = 48.sp,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 메인 텍스트 - 정중앙
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = if (onTextClick != null) {
                Modifier.clickable { onTextClick() }
            } else {
                Modifier
            },
            lineHeight = fontSize * 1.125f
        )
        
        // TTS 버튼 - 우측 절대 위치
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            UnifiedTTSButton(
                text = text,
                size = 50.dp
            )
        }
    }
} 