package com.lass.yomiyomi.ui.component.card

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
import com.lass.yomiyomi.domain.model.entity.Item
import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem
import com.lass.yomiyomi.ui.component.text.tts.KanjiTextWithAdaptiveTTS
import com.lass.yomiyomi.ui.component.text.tts.InfoRowWithTTS
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun KanjiCard(
    kanji: Item,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val speechManager = rememberSpeechManager()
    
    // Item 인터페이스를 통해 정보 가져오기
    val infoRows = kanji.toInfoRows()
    val mainText = kanji.getMainText()
    
    // 레벨 정보 찾기
    val level = infoRows.find { it.label.contains("레벨") }?.value ?: ""
    
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
            // Row 1: 레벨 배지, 메인 한자 + TTS, 편집/삭제 버튼(옵션)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽: 레벨 배지
                if (level.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = level,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 중앙: 한자 + TTS
                KanjiTextWithAdaptiveTTS(
                    text = mainText,
                    speechManager = speechManager,
                    onTextClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${mainText}".toUri()
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // 오른쪽: 편집/삭제 버튼 (있을 때만)
                if (onEdit != null || onDelete != null) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        onEdit?.let { editCallback ->
                            IconButton(
                                onClick = editCallback,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "편집",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        onDelete?.let { deleteCallback ->
                            IconButton(
                                onClick = deleteCallback,
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
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 나머지 정보들 표시 (레벨 제외)
            infoRows.filter { !it.label.contains("레벨") }.forEach { infoRow ->
                if (infoRow.value.isNotBlank()) {
                    if (infoRow.isJapanese) {
                        InfoRowWithTTS(
                            label = infoRow.label,
                            value = infoRow.value,
                            speechManager = speechManager
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${infoRow.label} ${infoRow.value}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// 호환성을 위한 별칭
@Composable
fun MyKanjiCard(
    myKanji: MyKanjiItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    KanjiCard(
        kanji = myKanji,
        onEdit = onEdit,
        onDelete = onDelete
    )
}

@Preview(showBackground = true)
@Composable
fun KanjiCardEditablePreview() {
    YomiYomiTheme {
        KanjiCard(
            kanji = MyKanjiItem(
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

@Preview(showBackground = true)
@Composable
fun KanjiCardReadOnlyPreview() {
    YomiYomiTheme {
        KanjiCard(
            kanji = KanjiItem(
                id = 1,
                kanji = "学",
                onyomi = "がく",
                kunyomi = "まな(ぶ)",
                meaning = "배우다, 학문",
                level = "N4",
                learningWeight = 0.6f,
                timestamp = System.currentTimeMillis()
            )
        )
    }
} 
