package ru.it.timeflow.presentation.analytics

import ru.it.timeflow.util.dayBoundsMillis
import ru.it.timeflow.util.monthBoundsMillis
import ru.it.timeflow.util.weekBoundsMillis

enum class AnalyticsPeriod(
    val title: String,
) {
    DAY("День"),
    WEEK("Неделя"),
    MONTH("Месяц");

    fun bounds(): Pair<Long, Long> =
        when (this) {
            DAY -> dayBoundsMillis()
            WEEK -> weekBoundsMillis()
            MONTH -> monthBoundsMillis()
        }
}