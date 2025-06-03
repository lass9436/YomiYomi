package com.lass.yomiyomi.ui.component.list

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.ui.component.tts.KanjiTextWithAdaptiveTTS
import com.lass.yomiyomi.ui.component.tts.InfoRowWithTTS
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun MyKanjiCard(
    myKanji: MyKanjiItem,
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
            // Row 1: 레벨 배지, 메인 한자 + TTS, 편집/삭제 버튼
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
                        text = myKanji.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 중앙: 통일된 한자 + TTS 컴포넌트 사용 (한자는 보통 1-2글자이므로 길이 기준을 2로 변경)
                KanjiTextWithAdaptiveTTS(
                    text = myKanji.kanji,
                    speechManager = speechManager,
                    onTextClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${myKanji.kanji}".toUri()
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

            // Row 2: 음독 + TTS (음독이 있는 경우에만)
            if (myKanji.onyomi.isNotBlank()) {
                InfoRowWithTTS(
                    label = "음독:",
                    value = myKanji.onyomi,
                    speechManager = speechManager
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Row 3: 훈독 + TTS (훈독이 있는 경우에만)
            if (myKanji.kunyomi.isNotBlank()) {
                InfoRowWithTTS(
                    label = "훈독:",
                    value = myKanji.kunyomi,
                    speechManager = speechManager
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Row 4: 의미 (TTS 없음)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "의미: ${myKanji.meaning}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyKanjiCardPreview() {
    YomiYomiTheme {
        MyKanjiCard(
            myKanji = MyKanjiItem(
                id = 1,
                kanji = "食",
                onyomi = "しょく",
                kunyomi = "た(べる)",
                meaning = "음식, 먹다",
                level = "N5",
                learningWeight = 0.8f,
                timestamp = System.currentTimeMillis()
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 
