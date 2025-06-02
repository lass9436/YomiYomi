package com.lass.yomiyomi.ui.component.quiz

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun QuizCard(
    question: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    searchUrl: String
) {
    val context = LocalContext.current
    
    // TTS 기능 - 직접 주입
    val speechManager = rememberSpeechManager()
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 질문과 TTS 버튼을 Box로 중앙에 배치
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 질문 텍스트 - 정중앙
                Text(
                    text = question,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "$searchUrl${question.split(" / ")[0]}".toUri()
                        )
                        context.startActivity(intent)
                    },
                    lineHeight = 36.sp,
                )
                
                // TTS 버튼 - 우측 절대 위치
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                ) {
                    TextToSpeechButton(
                        text = question,
                        isSpeaking = isSpeaking,
                        onSpeak = { originalText ->
                            val japaneseText = JapaneseTextFilter.prepareTTSText(originalText)
                            if (japaneseText.isNotEmpty()) {
                                speechManager.speakWithOriginalText(originalText, japaneseText)
                            }
                        },
                        onStop = { speechManager.stopSpeaking() },
                        size = 32.dp,
                        speechManager = speechManager
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, option ->
                QuizOptionButton(
                    option = option,
                    onOptionSelected = { onOptionSelected(index) }
                )
            }
        }
    }
} 