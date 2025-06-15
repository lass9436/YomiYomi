package com.lass.yomiyomi.ui.component.dialog.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "문단 리스트 선택",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 새 리스트 추가 버튼
                if (!showAddInput) {
                    TextButton(
                        onClick = { showAddInput = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "추가")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("새 리스트 추가")
                        }
                    }
                }

                // 새 리스트 추가 입력 필드
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
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
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
                        TextButton(
                            onClick = {
                                if (newListName.isNotBlank()) {
                                    onAddListClick(newListName)
                                    showAddInput = false
                                    newListName = ""
                                }
                            }
                        ) {
                            Text("추가")
                        }
                    }
                }

                // 리스트 목록
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(paragraphLists) { list ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkedListIds.contains(list.listId),
                                    onCheckedChange = { checked ->
                                        onCheckedChange(list.listId, checked)
                                    }
                                )
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
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
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
                                    onClick = { deletingListId = list.listId }
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
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onConfirm) {
                        Text("확인")
                    }
                }
            }
        }
    }

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
