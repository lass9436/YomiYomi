package com.lass.yomiyomi.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lass.yomiyomi.domain.model.SentenceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceInputDialog(
    isOpen: Boolean,
    sentence: SentenceItem? = null, // null이면 새로 생성, 값이 있으면 편집
    onDismiss: () -> Unit,
    onSave: (SentenceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return
    
    var japanese by remember(sentence) { mutableStateOf(sentence?.japanese ?: "") }
    var korean by remember(sentence) { mutableStateOf(sentence?.korean ?: "") }
    var category by remember(sentence) { mutableStateOf(sentence?.category ?: "일반") }
    var difficulty by remember(sentence) { mutableStateOf(sentence?.difficulty ?: "초급") }
    var showPreview by remember { mutableStateOf(false) }
    
    val categories = listOf("일반", "자기소개", "면접", "회화", "비즈니스", "일상", "여행")
    val difficulties = listOf("초급", "중급", "고급")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 제목
                Text(
                    text = if (sentence == null) "새 문장 추가" else "문장 편집",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 일본어 입력
                OutlinedTextField(
                    value = japanese,
                    onValueChange = { japanese = it },
                    label = { Text("일본어 (한자[요미가나] 형식으로 입력)") },
                    placeholder = { Text("私[わたし]は学生[がくせい]です") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                // 미리보기 버튼
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showPreview = !showPreview }
                    ) {
                        Icon(
                            if (showPreview) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showPreview) "미리보기 숨김" else "미리보기")
                    }
                }
                
                // 미리보기
                if (showPreview && japanese.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "미리보기:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FuriganaText(
                                japaneseText = japanese,
                                displayMode = com.lass.yomiyomi.ui.component.DisplayMode.FULL,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 한국어 번역
                OutlinedTextField(
                    value = korean,
                    onValueChange = { korean = it },
                    label = { Text("한국어 번역") },
                    placeholder = { Text("나는 학생입니다") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 카테고리 선택
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("카테고리") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 난이도 선택
                var difficultyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = difficultyExpanded,
                    onExpandedChange = { difficultyExpanded = !difficultyExpanded }
                ) {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("난이도") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = difficultyExpanded,
                        onDismissRequest = { difficultyExpanded = false }
                    ) {
                        difficulties.forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff) },
                                onClick = {
                                    difficulty = diff
                                    difficultyExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소")
                    }
                    
                    Button(
                        onClick = {
                            val newSentence = sentence?.copy(
                                japanese = japanese.trim(),
                                korean = korean.trim(),
                                category = category,
                                difficulty = difficulty
                            ) ?: SentenceItem(
                                id = 0, // 새 문장은 ID 0으로 시작
                                japanese = japanese.trim(),
                                korean = korean.trim(),
                                category = category,
                                difficulty = difficulty,
                                paragraphId = null, // 개별 문장
                                orderInParagraph = 0,
                                learningProgress = 0f,
                                reviewCount = 0,
                                createdAt = System.currentTimeMillis(),
                                lastReviewedAt = null
                            )
                            onSave(newSentence)
                        },
                        enabled = japanese.isNotBlank() && korean.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (sentence == null) "추가" else "저장")
                    }
                }
            }
        }
    }
} 