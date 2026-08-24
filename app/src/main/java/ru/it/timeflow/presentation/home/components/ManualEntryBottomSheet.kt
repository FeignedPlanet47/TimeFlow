package ru.it.timeflow.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.presentation.home.timeline.TimelineItem
import ru.it.timeflow.util.formatClockTime
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryBottomSheet(
    gap: TimelineItem.Gap,
    categories: List<Category>,
    onSave: (
        categoryId: Long,
        startMillis: Long,
        endMillis: Long,
        taskName: String?,
        note: String?,
    ) -> Unit,
    onDismiss: () -> Unit,
) {
    val roundedStart =
        remember(gap) {
            ceilToMinute(
                gap.startMillis
            )
        }

    val roundedEnd =
        remember(gap) {
            floorToMinute(
                gap.endMillis
            )
        }

    var selectedCategoryId by
    remember(gap) {
        mutableStateOf<Long?>(
            categories.firstOrNull()?.id
        )
    }

    var startText by
    remember(gap) {
        mutableStateOf(
            formatClockTime(
                roundedStart
            )
        )
    }

    var endText by
    remember(gap) {
        mutableStateOf(
            formatClockTime(
                roundedEnd
            )
        )
    }

    var taskName by
    remember(gap) {
        mutableStateOf("")
    }

    var note by
    remember(gap) {
        mutableStateOf("")
    }

    var validationError by
    remember(gap) {
        mutableStateOf<String?>(
            null
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                ),
        ) {
            Text(
                text =
                    "Добавить занятие вручную",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                text =
                    "Свободный промежуток: ${
                        formatClockTime(
                            gap.startMillis
                        )
                    } – ${
                        formatClockTime(
                            gap.endMillis
                        )
                    }",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                text = "Категория",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
            )

            Spacer(
                Modifier.height(8.dp)
            )

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    ),
            ) {
                items(
                    items = categories,
                    key = { it.id },
                ) { category ->

                    FilterChip(
                        selected =
                            selectedCategoryId ==
                                    category.id,
                        onClick = {
                            selectedCategoryId =
                                category.id
                        },
                        label = {
                            Text(
                                "${category.emoji} ${category.name}"
                            )
                        },
                    )
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    ),
            ) {
                OutlinedTextField(
                    value = startText,
                    onValueChange = {
                        startText = it
                        validationError =
                            null
                    },
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Начало")
                    },
                    placeholder = {
                        Text("09:30")
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number,
                        ),
                )

                OutlinedTextField(
                    value = endText,
                    onValueChange = {
                        endText = it
                        validationError =
                            null
                    },
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Конец")
                    },
                    placeholder = {
                        Text("11:00")
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number,
                        ),
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            OutlinedTextField(
                value = taskName,
                onValueChange = {
                    taskName = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Конкретная задача"
                    )
                },
                placeholder = {
                    Text(
                        "Например: разработка TimeFlow"
                    )
                },
                singleLine = true,
            )

            Spacer(
                Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Заметка")
                },
                placeholder = {
                    Text(
                        "Что было сделано"
                    )
                },
                minLines = 2,
                maxLines = 5,
            )

            validationError?.let {
                Spacer(
                    Modifier.height(10.dp)
                )

                Text(
                    text = it,
                    color =
                        MaterialTheme
                            .colorScheme
                            .error,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                )
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Button(
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    selectedCategoryId !=
                            null &&
                            categories.isNotEmpty(),
                onClick = {

                    val categoryId =
                        selectedCategoryId

                    if (categoryId == null) {
                        validationError =
                            "Выберите категорию."
                        return@Button
                    }

                    val startMillis =
                        parseClockTimeForGap(
                            text = startText,
                            gap = gap,
                        )

                    val endMillis =
                        parseClockTimeForGap(
                            text = endText,
                            gap = gap,
                        )

                    when {
                        startMillis == null ||
                                endMillis == null -> {
                            validationError =
                                "Введите время в формате ЧЧ:ММ, например 09:30."
                        }

                        endMillis <=
                                startMillis -> {
                            validationError =
                                "Время окончания должно быть позже начала."
                        }

                        startMillis <
                                gap.startMillis ||
                                endMillis >
                                gap.endMillis -> {
                            validationError =
                                "Занятие должно находиться внутри выбранного пустого промежутка."
                        }

                        else -> {
                            onSave(
                                categoryId,
                                startMillis,
                                endMillis,
                                taskName
                                    .trim()
                                    .takeIf {
                                        it.isNotEmpty()
                                    },
                                note
                                    .trim()
                                    .takeIf {
                                        it.isNotEmpty()
                                    },
                            )
                        }
                    }
                },
            ) {
                Text(
                    "Добавить занятие"
                )
            }

            Spacer(
                Modifier.height(32.dp)
            )
        }
    }
}

private fun ceilToMinute(
    millis: Long
): Long {
    val minute =
        60_000L

    return if (
        millis % minute == 0L
    ) {
        millis
    } else {
        (
                millis / minute +
                        1L
                ) * minute
    }
}

private fun floorToMinute(
    millis: Long
): Long =
    millis / 60_000L * 60_000L

private fun parseClockTimeForGap(
    text: String,
    gap: TimelineItem.Gap,
): Long? {
    return try {
        val formatter =
            DateTimeFormatter.ofPattern(
                "HH:mm"
            )

        val raw =
            text.trim()

        val normalized =
            if (
                raw.length == 4 &&
                raw.all { it.isDigit() }
            ) {
                "${raw.take(2)}:${raw.takeLast(2)}"
            } else {
                raw
            }

        val localTime =
            LocalTime.parse(
                normalized,
                formatter,
            )

        val zone =
            ZoneId.systemDefault()

        val date =
            Instant
                .ofEpochMilli(
                    gap.startMillis
                )
                .atZone(zone)
                .toLocalDate()

        date
            .atTime(localTime)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

    } catch (_: DateTimeParseException) {
        null
    }
}
