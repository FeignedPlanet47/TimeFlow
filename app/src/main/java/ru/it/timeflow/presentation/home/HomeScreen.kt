package ru.it.timeflow.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.model.TimeEntry
import ru.it.timeflow.presentation.home.components.TaskPickerBottomSheet
import ru.it.timeflow.util.formatClockTime
import ru.it.timeflow.util.formatDuration
import ru.it.timeflow.util.formatDurationCompact

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by
    viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onCategoryClick = viewModel::start,
        onCategoryLongClick =
            viewModel::openTaskPicker,
        onTaskClick = viewModel::startTask,
        onAddTask = viewModel::addTask,
        onDismissTaskPicker =
            viewModel::closeTaskPicker,
        onSaveNote = viewModel::saveNote,
        onStopClick = viewModel::stop,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onCategoryClick: (Long) -> Unit,
    onCategoryLongClick: (Long) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTask: (String) -> Unit,
    onDismissTaskPicker: () -> Unit,
    onSaveNote: (Long, String) -> Unit,
    onStopClick: () -> Unit,
) {
    if (state.isLoading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    "Сегодня",
                    style =
                        MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    "Учтено ${
                        formatDurationCompact(
                            state.todayTrackedMillis
                        )
                    }",
                    style =
                        MaterialTheme.typography.bodyLarge,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                ActiveTimerCard(
                    entry = state.activeEntry,
                    nowMillis = state.nowMillis,
                    onSaveNote = onSaveNote,
                    onStop = onStopClick,
                )
            }

            item {
                Text(
                    "Начать занятие",
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        state.categories,
                        key = { it.id },
                    ) { category ->

                        CategoryChip(
                            category = category,
                            onClick = onCategoryClick,
                            onLongClick =
                                onCategoryLongClick,
                        )
                    }
                }

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Нажмите — начать. Удерживайте — выбрать конкретную задачу.",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Text(
                    "Последние",
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            items(
                state.todayEntries.take(8),
                key = { it.id },
            ) { entry ->
                EntryCard(
                    entry = entry,
                    nowMillis = state.nowMillis,
                )
            }

            if (state.todayEntries.isEmpty()) {
                item {
                    Text(
                        "Пока нет записей. Выберите занятие выше — таймер запустится сразу.",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    state.taskPickerCategory?.let { category ->

        TaskPickerBottomSheet(
            categoryName =
                "${category.emoji} ${category.name}",
            tasks = state.tasksForPicker,
            onTaskClick = onTaskClick,
            onAddTask = onAddTask,
            onDismiss = onDismissTaskPicker,
        )
    }
}

@Composable
private fun ActiveTimerCard(
    entry: TimeEntry?,
    nowMillis: Long,
    onSaveNote: (Long, String) -> Unit,
    onStop: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            if (entry == null) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Таймер не запущен",
                    style =
                        MaterialTheme.typography.titleLarge,
                )

                Text(
                    "Выберите занятие ниже",
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {

                var noteText by remember(
                    entry.id,
                    entry.note,
                ) {
                    mutableStateOf(
                        entry.note.orEmpty()
                    )
                }

                Text(
                    "${entry.categoryEmoji} ${entry.categoryName}",
                    style =
                        MaterialTheme.typography.titleLarge,
                )

                entry.taskName?.let { taskName ->

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        text = taskName,
                        style =
                            MaterialTheme.typography.titleMedium,
                        color =
                            MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text =
                        formatDuration(
                            entry.durationMillis(
                                nowMillis
                            )
                        ),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    "Начато в ${
                        formatClockTime(
                            entry.startTimeMillis
                        )
                    }"
                )

                Spacer(Modifier.height(18.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Заметка")
                    },
                    placeholder = {
                        Text(
                            "Например: доделать экран статистики"
                        )
                    },
                    minLines = 2,
                    maxLines = 5,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        enabled =
                            noteText.trim() !=
                                    entry.note.orEmpty().trim(),
                        onClick = {
                            onSaveNote(
                                entry.id,
                                noteText,
                            )
                        },
                    ) {
                        Text("Сохранить заметку")
                    }
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onStop,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.error
                        ),
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = null,
                    )

                    Spacer(Modifier.size(8.dp))

                    Text("Завершить")
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    onClick: (Long) -> Unit,
    onLongClick: (Long) -> Unit,
) {
    val color =
        Color(category.colorArgb)

    Row(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.14f),
                RoundedCornerShape(20.dp),
            )
            .combinedClickable(
                onClick = {
                    onClick(category.id)
                },
                onLongClick = {
                    onLongClick(category.id)
                },
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(10.dp)
                .background(
                    color,
                    CircleShape,
                )
        )

        Text(
            "${category.emoji} ${category.name}",
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EntryCard(
    entry: TimeEntry,
    nowMillis: Long,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .background(
                        Color(
                            entry.categoryColorArgb
                        ).copy(alpha = 0.16f),
                        CircleShape,
                    ),
                contentAlignment =
                    Alignment.Center,
            ) {
                Text(
                    entry.categoryEmoji,
                    fontSize = 22.sp,
                )
            }

            Spacer(Modifier.size(12.dp))

            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    entry.categoryName,
                    fontWeight =
                        FontWeight.SemiBold,
                )

                entry.taskName?.let {
                    Text(
                        text = it,
                        color =
                            MaterialTheme.colorScheme.primary,
                        style =
                            MaterialTheme.typography.bodyMedium,
                    )
                }

                entry.note?.let { note ->
                    Text(
                        text = note,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }

                Text(
                    "${
                        formatClockTime(
                            entry.startTimeMillis
                        )
                    } – ${
                        entry.endTimeMillis
                            ?.let(::formatClockTime)
                            ?: "сейчас"
                    }",
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                formatDurationCompact(
                    entry.durationMillis(
                        nowMillis
                    )
                ),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
