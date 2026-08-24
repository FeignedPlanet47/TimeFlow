package ru.it.timeflow.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.it.timeflow.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPickerBottomSheet(
    categoryName: String,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onAddTask: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var newTaskName by remember {
        mutableStateOf("")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Выберите конкретную задачу",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = "В этой категории пока нет задач",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn {
                    items(
                        items = tasks,
                        key = { it.id },
                    ) { task ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTaskClick(task)
                                }
                                .padding(vertical = 18.dp),
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = task.name,
                                style =
                                    MaterialTheme.typography.titleMedium,
                            )

                            Text(
                                text = "Начать",
                                color =
                                    MaterialTheme.colorScheme.primary,
                            )
                        }

                        HorizontalDivider()
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Новая задача",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = newTaskName,
                onValueChange = {
                    newTaskName = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        "Например, разработка TimeFlow"
                    )
                },
            )

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = newTaskName.isNotBlank(),
                onClick = {
                    onAddTask(newTaskName)
                    newTaskName = ""
                },
            ) {
                Text("Добавить задачу")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
