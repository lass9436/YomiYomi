package com.lass.yomiyomi.ui.component.text.tts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.speech.SpeechManager

/**
 * 한자 카드의 TTS 레이아웃을 위한 컴포넌트
 * 한자는 보통 1-2글자이므로 길이 기준을 2로 조정
 */
@Composable
fun KanjiTextWithAdaptiveTTS(
    text: String,
    speechManager: SpeechManager,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }
    
    if (text.length >= 2) {
        // 긴 텍스트(2글자 이상): TTS를 아래 중앙에
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = if (onTextClick != null) {
                    Modifier.clickable { onTextClick() }
                } else {
                    Modifier
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            UnifiedTTSButton(
                text = text,
                speechManager = speechManager,
                size = 28.dp
            )
        }
    } else {
        // 짧은 텍스트(1글자): TTS를 텍스트 옆에
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .onSizeChanged { size ->
                        textWidth = with(density) { size.width.toDp() }
                    }
                    .then(
                        if (onTextClick != null) {
                            Modifier.clickable { onTextClick() }
                        } else {
                            Modifier
                        }
                    )
            )
            
            if (textWidth > 0.dp) {
                UnifiedTTSButton(
                    text = text,
                    speechManager = speechManager,
                    size = 32.dp,
                    modifier = Modifier.offset(x = textWidth / 2 + 15.dp)
                )
            }
        }
    }
} 