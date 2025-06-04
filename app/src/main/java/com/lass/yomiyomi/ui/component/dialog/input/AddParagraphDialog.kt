package com.lass.yomiyomi.ui.component.dialog.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.constant.Level

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphInputDialog(
    isOpen: Boolean,
    paragraph: ParagraphItem? = null, // null이면 새로 생성, 값이 있으면 편집
    onDismiss: () -> Unit,
    onSave: (ParagraphItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return
    
    var title by remember(paragraph) { mutableStateOf(paragraph?.title ?: "") }
    var description by remember(paragraph) { mutableStateOf(paragraph?.description ?: "") }
    var category by remember(paragraph) { mutableStateOf(paragraph?.category ?: "일반") }
    var level by remember(paragraph) { mutableStateOf(paragraph?.level ?: Level.N5) }
    var totalSentences by remember(paragraph) { mutableStateOf(paragraph?.totalSentences?.toString() ?: "5") }
    
    val categories = listOf("일반", "자기소개", "면접", "회화", "비즈니스", "일상", "여행")
    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1)
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 제목
                Text(
                    text = if (paragraph == null) "새 문단 추가" else "문단 편집",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 문단 제목 입력
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("문단 제목") },
                    placeholder = { Text("예: 자기소개, 면접 준비 등") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 문단 설명 입력
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("문단 설명 (선택사항)") },
                    placeholder = { Text("이 문단에 대한 간단한 설명을 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 목표 문장 수
                OutlinedTextField(
                    value = totalSentences,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() } && it.length <= 3) {
                            totalSentences = it
                        }
                    },
                    label = { Text("목표 문장 수") },
                    placeholder = { Text("5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                
                // 레벨 선택
                var levelExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = levelExpanded,
                    onExpandedChange = { levelExpanded = !levelExpanded }
                ) {
                    OutlinedTextField(
                        value = level.value ?: "ALL",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("레벨") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
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
                            val sentenceCount = totalSentences.toIntOrNull() ?: 5
                            val newParagraph = paragraph?.copy(
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                level = level,
                                totalSentences = sentenceCount
                            ) ?: ParagraphItem(
                                paragraphId = "", // 새 문단은 빈 ID로 시작 (Repository에서 생성)
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                level = level,
                                totalSentences = sentenceCount,
                                actualSentenceCount = 0,
                                createdAt = System.currentTimeMillis()
                            )
                            onSave(newParagraph)
                        },
                        enabled = title.isNotBlank() && totalSentences.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text(if (paragraph == null) "추가" else "저장")
                    }
                }
            }
        }
    }
} 
