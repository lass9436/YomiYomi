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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem

/**
 * 문단을 여러 리스트에 추가/제거/수정/생성할 수 있는 공용 다이얼로그
 */
@Composable
fun ParagraphListDialog(
    paragraphLists: List<ParagraphListItem>,
    checkedListIds: List<Int>,
    onCheckedChange: (Int, Boolean) -> Unit,
    onAddListClick: (String) -> Unit,
    onEditListClick: (Int, String) -> Unit,
    onDeleteListClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showAddInput by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var editingListId by remember { mutableStateOf<Int?>(null) }
    var editingName by remember { mutableStateOf("") }
    var deletingListId by remember { mutableStateOf<Int?>(null) }

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
                                    value = checkedListIds.contains(list.listId),
                                    onValueChange = { checked ->
                                        onCheckedChange(list.listId, checked)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 체크박스
                            Checkbox(
                                checked = checkedListIds.contains(list.listId),
                                onCheckedChange = { checked ->
                                    onCheckedChange(list.listId, checked)
                                }
                            )

                            // 리스트 이름
                            if (editingListId == list.listId) {
                                OutlinedTextField(
                                    value = editingName,
                                    onValueChange = { editingName = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (editingName.isNotBlank()) {
                                                onEditListClick(list.listId, editingName)
                                                editingListId = null
                                                editingName = ""
                                            }
                                        }
                                    )
                                )
                            } else {
                                Text(
                                    text = list.name,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                )
                            }

                            // 편집/삭제 버튼
                            Row {
                                IconButton(
                                    onClick = {
                                        editingListId = list.listId
                                        editingName = list.name
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "편집",
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        deletingListId = list.listId
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "삭제",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // 새 리스트 추가 입력 필드
                    item {
                        if (showAddInput) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newListName,
                                    onValueChange = { newListName = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                    singleLine = true,
                                    placeholder = { Text("새 리스트 이름") },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (newListName.isNotBlank()) {
                                                onAddListClick(newListName)
                                                showAddInput = false
                                                newListName = ""
                                            }
                                        }
                                    )
                                )
                            }
                        } else {
                            TextButton(
                                onClick = { showAddInput = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "추가")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("새 리스트 추가")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )

    // 삭제 확인 다이얼로그
    deletingListId?.let { listId ->
        AlertDialog(
            onDismissRequest = { deletingListId = null },
            title = { Text("리스트 삭제") },
            text = { Text("이 리스트를 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteListClick(listId)
                        deletingListId = null
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingListId = null }) {
                    Text("취소")
                }
            }
        )
    }
}

// 리스트 항목 UI 데이터 클래스, 필요시 프로젝트에 맞게 변경해도 됩니다.
data class ParagraphListItem(
    val listId: Int,
    val name: String
)
