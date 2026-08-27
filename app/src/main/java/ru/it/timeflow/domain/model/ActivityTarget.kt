package ru.it.timeflow.domain.model

enum class ActivityTargetType(
    val title: String,
) {
    GOAL("Цель"),
    LIMIT("Лимит"),
}

enum class ActivityTargetPeriod(
    val title: String,
    val shortTitle: String,
) {
    DAY(
        title = "День",
        shortTitle = "в день",
    ),
    WEEK(
        title = "Неделя",
        shortTitle = "в неделю",
    ),
    MONTH(
        title = "Месяц",
        shortTitle = "в месяц",
    ),
}

data class ActivityTarget(
    val categoryId: Long,
    val type: ActivityTargetType,
    val period: ActivityTargetPeriod,
    val targetMillis: Long,
)
