package ru.it.timeflow.presentation.analytics.components

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
import androidx.compose.material3.OutlinedButton
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
import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.model.ActivityTargetPeriod
import ru.it.timeflow.domain.model.ActivityTargetType
import ru.it.timeflow.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTargetBottomSheet(
    categories: List<Category>,
    editingTarget: ActivityTarget?,
    usedCategoryIds: Set<Long>,
    onSave: (
        categoryId: Long,
        type: ActivityTargetType,
        period: ActivityTargetPeriod,
        targetMillis: Long,
    ) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val editingCategory =
        remember(
            editingTarget,
            categories,
        ) {
            editingTarget
                ?.let { target ->
                    categories
                        .firstOrNull {
                            it.id ==
                                    target.categoryId
                        }
                }
        }

    val availableCategories =
        remember(
            categories,
            usedCategoryIds,
            editingTarget,
        ) {
            if (
                editingTarget != null
            ) {
                categories
            } else {
                categories.filter {
                    it.id !in
                            usedCategoryIds
                }
            }
        }

    var selectedCategoryId by
    remember(
        editingTarget,
        availableCategories,
    ) {
        mutableStateOf<Long?>(
            editingTarget
                ?.categoryId
                ?: availableCategories
                    .firstOrNull()
                    ?.id
        )
    }

    var selectedType by
    remember(editingTarget) {
        mutableStateOf(
            editingTarget?.type
                ?: ActivityTargetType.GOAL
        )
    }

    var selectedPeriod by
    remember(editingTarget) {
        mutableStateOf(
            editingTarget?.period
                ?: ActivityTargetPeriod.WEEK
        )
    }

    val initialMinutes =
        (
                editingTarget
                    ?.targetMillis
                    ?: 0L
                ) / MILLIS_PER_MINUTE

    var hoursText by
    remember(editingTarget) {
        mutableStateOf(
            if (
                initialMinutes >
                0L
            ) {
                (
                        initialMinutes /
                                60L
                        ).toString()
            } else {
                ""
            }
        )
    }

    var minutesText by
    remember(editingTarget) {
        mutableStateOf(
            if (
                initialMinutes >
                0L
            ) {
                (
                        initialMinutes %
                                60L
                        ).toString()
            } else {
                ""
            }
        )
    }

    var validationError by
    remember(editingTarget) {
        mutableStateOf<String?>(
            null
        )
    }

    ModalBottomSheet(
        onDismissRequest =
            onDismiss,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            20.dp
                    ),
        ) {
            Text(
                text =
                    if (
                        editingTarget ==
                        null
                    ) {
                        "Новая цель или лимит"
                    } else {
                        "Изменить правило"
                    },
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
            )

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text = "Занятие",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
            )

            Spacer(
                Modifier.height(8.dp)
            )

            if (
                editingTarget != null
            ) {
                Text(
                    text =
                        editingCategory
                            ?.let {
                                "${it.emoji} ${it.name}"
                            }
                            ?: "Категория",
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                )
            } else if (
                availableCategories
                    .isEmpty()
            ) {
                Text(
                    text =
                        "Для всех категорий уже настроено правило.",
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )
            } else {
                LazyRow(
                    horizontalArrangement =
                        Arrangement
                            .spacedBy(
                                8.dp
                            ),
                ) {
                    items(
                        items =
                            availableCategories,
                        key = {
                            it.id
                        },
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
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text =
                    "Что установить",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    ),
            ) {
                ActivityTargetType
                    .entries
                    .forEach { type ->

                        FilterChip(
                            selected =
                                selectedType ==
                                        type,
                            onClick = {
                                selectedType =
                                    type
                            },
                            label = {
                                Text(
                                    type.title
                                )
                            },
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                        )
                    }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text = "Период",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    ),
            ) {
                ActivityTargetPeriod
                    .entries
                    .forEach { period ->

                        FilterChip(
                            selected =
                                selectedPeriod ==
                                        period,
                            onClick = {
                                selectedPeriod =
                                    period
                            },
                            label = {
                                Text(
                                    period.title
                                )
                            },
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                        )
                    }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text =
                    if (
                        selectedType ==
                        ActivityTargetType.GOAL
                    ) {
                        "Сколько времени хотите уделять"
                    } else {
                        "Сколько времени максимум"
                    },
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
            )

            Spacer(
                Modifier.height(8.dp)
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
                    value =
                        hoursText,
                    onValueChange = {
                        hoursText =
                            it.filter(
                                Char::isDigit
                            )
                        validationError =
                            null
                    },
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Часы")
                    },
                    placeholder = {
                        Text("5")
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                )

                OutlinedTextField(
                    value =
                        minutesText,
                    onValueChange = {
                        minutesText =
                            it.filter(
                                Char::isDigit
                            )
                        validationError =
                            null
                    },
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Минуты")
                    },
                    placeholder = {
                        Text("30")
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                )
            }

            validationError?.let {
                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text = it,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .error,
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
                            null,
                onClick = {
                    val categoryId =
                        selectedCategoryId

                    if (
                        categoryId == null
                    ) {
                        validationError =
                            "Выберите занятие."
                        return@Button
                    }

                    val hours =
                        hoursText
                            .toLongOrNull()
                            ?: 0L

                    val minutes =
                        minutesText
                            .toLongOrNull()
                            ?: 0L

                    if (
                        minutes !in
                        0L..59L
                    ) {
                        validationError =
                            "Минуты должны быть от 0 до 59."
                        return@Button
                    }

                    val totalMinutes =
                        hours * 60L +
                                minutes

                    if (
                        totalMinutes <= 0L
                    ) {
                        validationError =
                            "Укажите время больше нуля."
                        return@Button
                    }

                    onSave(
                        categoryId,
                        selectedType,
                        selectedPeriod,
                        totalMinutes *
                                MILLIS_PER_MINUTE,
                    )
                },
            ) {
                Text(
                    if (
                        editingTarget ==
                        null
                    ) {
                        "Добавить"
                    } else {
                        "Сохранить"
                    }
                )
            }

            if (
                editingTarget != null
            ) {
                Spacer(
                    Modifier.height(10.dp)
                )

                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    onClick = {
                        onDelete(
                            editingTarget
                                .categoryId
                        )
                    },
                ) {
                    Text(
                        text =
                            "Удалить правило",
                        color =
                            MaterialTheme
                                .colorScheme
                                .error,
                    )
                }
            }

            Spacer(
                Modifier.height(32.dp)
            )
        }
    }
}

private const val
        MILLIS_PER_MINUTE =
    60_000L
