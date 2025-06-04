package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.ui.component.button.SpeechQuizButton

@Composable
fun SentenceQuizContent(
    isLoading: Boolean,
    question: String?,
    isListening: Boolean,
    recognizedText: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCheckAnswer: (String) -> Unit,
    insufficientDataMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 600.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            insufficientDataMessage != null -> {
                Text(
                    text = insufficientDataMessage,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            question != null -> {
                SentenceQuizCard(
                    question = question,
                    isListening = isListening,
                    recognizedText = recognizedText,
                    onStartListening = onStartListening,
                    onStopListening = onStopListening,
                    onCheckAnswer = onCheckAnswer
                )
            }
            else -> {
                Text(
                    text = "퀴즈 로드 실패",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SentenceQuizCard(
    question: String,
    isListening: Boolean,
    recognizedText: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCheckAnswer: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 문제 텍스트
        Text(
            text = question,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 음성 인식 버튼
        SpeechQuizButton(
            isListening = isListening,
            recognizedText = recognizedText,
            onStartListening = onStartListening,
            onStopListening = onStopListening,
            onCheckAnswer = onCheckAnswer
        )
    }
} 