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
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.util.FuriganaParser

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
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            insufficientDataMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = insufficientDataMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            question != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 문제 텍스트 - 후리가나가 포함되어 있으면 FuriganaText 사용
                    if (FuriganaParser.hasKanji(question)) {
                        FuriganaText(
                            japaneseText = question,
                            displayMode = DisplayMode.FULL,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    } else {
                        Text(
                            text = question,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                    
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
        }
    }
} 