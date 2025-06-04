package com.lass.yomiyomi.ui.component.dialog.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceInputDialog(
    isOpen: Boolean,
    sentence: SentenceItem? = null,
    availableCategories: List<String> = emptyList(),
    availableLevels: List<Level> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (SentenceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return
    
    var japanese by remember(sentence) { mutableStateOf(sentence?.japanese ?: "") }
    var korean by remember(sentence) { mutableStateOf(sentence?.korean ?: "") }
    var category by remember(sentence) { mutableStateOf(sentence?.category ?: availableCategories.firstOrNull() ?: "일반") }
    var level by remember(sentence) { mutableStateOf(sentence?.level ?: availableLevels.firstOrNull() ?: Level.N5) }
    var showPreview by remember { mutableStateOf(false) }
    
    // 기본값을 포함한 카테고리/레벨 목록 (기존 값이 없으면 기본값 추가)
    val categories = if (availableCategories.isNotEmpty()) {
        availableCategories
    } else {
        listOf("일반", "자기소개", "면접", "회화", "비즈니스", "일상", "여행") // 폴백 옵션
    }
    
    val levels = if (availableLevels.isNotEmpty()) {
        availableLevels 
    } else {
        listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1) // 폴백 옵션
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (sentence == null) "문장 추가" else "문장 편집",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
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
                        onClick = { showPreview = !showPreview },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
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
                                displayMode = DisplayMode.FULL,
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
                
                // 카테고리 선택 (독립 문장일 때만 표시)
                if (availableCategories.isNotEmpty()) {
                    var categoryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("카테고리") },
                            placeholder = { Text("카테고리를 선택하거나 새로 입력하세요") },
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
                }
                
                // 레벨 선택 (독립 문장일 때만 표시)
                if (availableLevels.isNotEmpty()) {
                    var levelExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = levelExpanded,
                        onExpandedChange = { levelExpanded = !levelExpanded }
                    ) {
                        OutlinedTextField(
                            value = level.value ?: "ALL",
                            onValueChange = { },
                            label = { Text("레벨") },
                            placeholder = { Text("레벨을 선택하세요") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = levelExpanded,
                            onDismissRequest = { levelExpanded = false }
                        ) {
                            levels.forEach { lv ->
                                DropdownMenuItem(
                                    text = { Text(lv.value ?: "ALL") },
                                    onClick = {
                                        level = lv
                                        levelExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    // 문단 소속 문장일 때 안내 텍스트
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "📝 이 문장은 문단에 속하므로 문단의 카테고리와 레벨을 따릅니다.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("취소")
                    }
                    
                    Button(
                        onClick = {
                            val newSentence = sentence?.copy(
                                japanese = japanese.trim(),
                                korean = korean.trim(),
                                category = category,
                                level = level
                            ) ?: SentenceItem(
                                id = 0, // 새 문장은 ID 0으로 시작 (Repository에서 생성)
                                japanese = japanese.trim(),
                                korean = korean.trim(),
                                category = category,
                                level = level,
                                paragraphId = null,
                                orderInParagraph = 0,
                                learningProgress = 0f,
                                reviewCount = 0,
                                createdAt = System.currentTimeMillis(),
                                lastReviewedAt = null
                            )
                            onSave(newSentence)
                        },
                        enabled = japanese.isNotBlank() && korean.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text(if (sentence == null) "추가" else "저장")
                    }
                }
            }
        }
    }
} 
