package com.lass.yomiyomi.ui.component.list

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.ui.component.tts.WordTextWithAdaptiveTTS
import com.lass.yomiyomi.ui.component.tts.InfoRowWithTTS
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun MyWordCard(
    myWord: MyWordItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val speechManager = rememberSpeechManager()
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row 1: 레벨 배지, 메인 단어 + TTS, 편집/삭제 버튼
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽: 레벨 배지
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = myWord.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 중앙: 통일된 단어 + TTS 컴포넌트 사용
                WordTextWithAdaptiveTTS(
                    text = myWord.word,
                    speechManager = speechManager,
                    onTextClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${myWord.word}".toUri()
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // 오른쪽: 편집/삭제 버튼
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "편집",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Row 2: 읽기 + TTS (읽기가 단어와 다른 경우에만)
            if (myWord.reading != myWord.word && myWord.reading.isNotBlank()) {
                InfoRowWithTTS(
                    label = "읽기:",
                    value = myWord.reading,
                    speechManager = speechManager
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Row 3: 의미와 품사
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "의미: ${myWord.meaning}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                
                // 품사를 쉼표로 split해서 작은 카드들로 표시
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    myWord.type.split(",").forEach { type ->
                        val trimmedType = type.trim()
                        if (trimmedType.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = trimmedType,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyWordCardPreview() {
    YomiYomiTheme {
        MyWordCard(
            myWord = MyWordItem(
                id = 1,
                word = "こんにちは",
                reading = "こんにちは",
                meaning = "안녕하세요",
                level = "N5",
                learningWeight = 0.9f,
                timestamp = System.currentTimeMillis(),
                type = "동사"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 
