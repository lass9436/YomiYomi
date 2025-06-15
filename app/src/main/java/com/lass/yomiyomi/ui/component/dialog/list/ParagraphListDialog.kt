package com.lass.yomiyomi.ui.component.dialog.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * 문단을 여러 리스트에 추가/제거/수정/생성할 수 있는 공용 다이얼로그
 */
@Composable
fun ParagraphListDialog(
    paragraphLists: List<ParagraphListUiModel>,
    checkedListIds: List<Long>,
    onCheckedChange: (listId: Long, Boolean) -> Unit,
    onAddListClick: (String) -> Unit,
    onEditListClick: (listId: Long, newName: String) -> Unit,
    onDeleteListClick: (listId: Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showAddInput by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var editingListId by remember { mutableStateOf<Long?>(null) }
    var editingName by remember { mutableStateOf("") }
    var deletingListId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("문단 보관함 담기", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                // 리스트 목록
                LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                    items(paragraphLists.size) { idx ->
                        val list = paragraphLists[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .toggleable(
                                    value = checkedListIds.contains(list.id),
                                    onValueChange = { checked ->
                                        onCheckedChange(list.id, checked)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checkedListIds.contains(list.id),
                                onCheckedChange = null // Row 전체가 toggleable이므로
                            )
                            Text(
                                text = list.name,
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                                maxLines = 1
                            )
                            IconButton(onClick = {
                                editingListId = list.id
                                editingName = list.name
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "이름 수정")
                            }
                            IconButton(onClick = {
                                deletingListId = list.id
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "리스트 삭제", tint = Color.Red)
                            }
                        }
                    }
                }

                // 리스트 추가
                if (showAddInput) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newListName,
                            onValueChange = { newListName = it },
                            placeholder = { Text("새 리스트 이름") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newListName.isNotBlank()) {
                                    onAddListClick(newListName.trim())
                                    newListName = ""
                                    showAddInput = false
                                }
                            })
                        )
                        IconButton(
                            onClick = {
                                if (newListName.isNotBlank()) {
                                    onAddListClick(newListName.trim())
                                    newListName = ""
                                    showAddInput = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "리스트 생성")
                        }
                    }
                } else {
                    TextButton(onClick = { showAddInput = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("새 리스트 생성")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = checkedListIds.isNotEmpty()
            ) { Text("담기") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )

    // 이름 수정 다이얼로그
    if (editingListId != null) {
        AlertDialog(
            onDismissRequest = { editingListId = null },
            title = { Text("리스트 이름 수정") },
            text = {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { editingName = it },
                    placeholder = { Text("이름 입력") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editingName.isNotBlank()) {
                            onEditListClick(editingListId!!, editingName.trim())
                            editingListId = null
                        }
                    }
                ) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { editingListId = null }) {
                    Text("취소")
                }
            }
        )
    }

    // 삭제 확인 다이얼로그
    if (deletingListId != null) {
        AlertDialog(
            onDismissRequest = { deletingListId = null },
            title = { Text("리스트 삭제") },
            text = { Text("정말 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteListClick(deletingListId!!)
                        deletingListId = null
                    }
                ) { Text("삭제", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deletingListId = null }) { Text("취소") }
            }
        )
    }
}

// 리스트 항목 UI 데이터 클래스, 필요시 프로젝트에 맞게 변경해도 됩니다.
data class ParagraphListUiModel(
    val id: Long,
    val name: String
)
