package ru.it.timeflow.presentation.analytics

import ru.it.timeflow.domain.model.TimeEntry
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class WeekComparisonResult(
    val items: List<WeekComparisonItem>,
    val summary: String?,
)

fun weekComparisonQueryStart(
    nowMillis: Long,
): Long {
    return comparisonBounds(
        nowMillis
    ).previousStart
}

fun buildWeekComparison(
    entries: List<TimeEntry>,
    nowMillis: Long,
): WeekComparisonResult {

    val bounds =
        comparisonBounds(nowMillis)

    val current =
        categoryDurations(
            entries = entries,
            startMillis =
                bounds.currentStart,
            endMillis =
                bounds.currentEnd,
            nowMillis =
                nowMillis,
        )

    val previous =
        categoryDurations(
            entries = entries,
            startMillis =
                bounds.previousStart,
            endMillis =
                bounds.previousEnd,
            nowMillis =
                nowMillis,
        )

    val entriesByCategory =
        entries.groupBy {
            it.categoryId
        }

    val categoryIds =
        (
                current.keys +
                        previous.keys
                ).toSet()

    val items =
        categoryIds
            .mapNotNull { categoryId ->

                val reference =
                    entriesByCategory[
                        categoryId
                    ]
                        ?.firstOrNull()
                        ?: return@mapNotNull null

                val currentMillis =
                    current[
                        categoryId
                    ] ?: 0L

                val previousMillis =
                    previous[
                        categoryId
                    ] ?: 0L

                val difference =
                    currentMillis -
                            previousMillis

                val direction =
                    when {
                        previousMillis == 0L &&
                                currentMillis > 0L ->
                            WeekChangeDirection.NEW

                        difference > 0L ->
                            WeekChangeDirection.UP

                        difference < 0L ->
                            WeekChangeDirection.DOWN

                        else ->
                            WeekChangeDirection.SAME
                    }

                val percent =
                    if (
                        previousMillis > 0L
                    ) {
                        (
                                difference.toDouble() /
                                        previousMillis.toDouble() *
                                        100.0
                                ).toFloat()
                    } else {
                        null
                    }

                WeekComparisonItem(
                    categoryId =
                        categoryId,
                    name =
                        reference.categoryName,
                    emoji =
                        reference.categoryEmoji,
                    colorArgb =
                        reference.categoryColorArgb,
                    currentWeekMillis =
                        currentMillis,
                    previousWeekMillis =
                        previousMillis,
                    differenceMillis =
                        difference,
                    changePercent =
                        percent,
                    direction =
                        direction,
                )
            }
            .sortedByDescending {
                abs(
                    it.differenceMillis
                )
            }

    return WeekComparisonResult(
        items = items,
        summary =
            buildSummary(items),
    )
}

private fun comparisonBounds(
    nowMillis: Long,
): ComparisonBounds {

    val zone =
        ZoneId.systemDefault()

    val now =
        Instant
            .ofEpochMilli(nowMillis)
            .atZone(zone)

    val currentWeekStart =
        now
            .toLocalDate()
            .with(
                TemporalAdjusters
                    .previousOrSame(
                        DayOfWeek.MONDAY
                    )
            )
            .atStartOfDay(zone)

    val currentStart =
        currentWeekStart
            .toInstant()
            .toEpochMilli()

    val elapsed =
        (
                nowMillis -
                        currentStart
                ).coerceAtLeast(0L)

    val previousStart =
        currentWeekStart
            .minusWeeks(1)
            .toInstant()
            .toEpochMilli()

    return ComparisonBounds(
        currentStart =
            currentStart,
        currentEnd =
            nowMillis,
        previousStart =
            previousStart,
        previousEnd =
            previousStart +
                    elapsed,
    )
}

private fun categoryDurations(
    entries: List<TimeEntry>,
    startMillis: Long,
    endMillis: Long,
    nowMillis: Long,
): Map<Long, Long> {

    return entries
        .groupBy {
            it.categoryId
        }
        .mapValues {
                (_, list) ->

            list.sumOf { entry ->
                overlapDuration(
                    entry = entry,
                    startMillis =
                        startMillis,
                    endMillis =
                        endMillis,
                    nowMillis =
                        nowMillis,
                )
            }
        }
        .filterValues {
            it > 0L
        }
}

private fun overlapDuration(
    entry: TimeEntry,
    startMillis: Long,
    endMillis: Long,
    nowMillis: Long,
): Long {

    val start =
        max(
            entry.startTimeMillis,
            startMillis,
        )

    val end =
        min(
            entry.endTimeMillis
                ?: nowMillis,
            endMillis,
        )

    return (
            end - start
            ).coerceAtLeast(0L)
}

private fun buildSummary(
    items: List<WeekComparisonItem>,
): String? {

    if (items.isEmpty()) {
        return null
    }

    val meaningful =
        items.filter {
            abs(
                it.differenceMillis
            ) >=
                    30L *
                    60L *
                    1_000L
        }

    if (meaningful.isEmpty()) {
        return "Эта неделя пока почти не отличается от прошлой."
    }

    val decrease =
        meaningful
            .filter {
                it.differenceMillis < 0L
            }
            .maxByOrNull {
                abs(
                    it.differenceMillis
                )
            }

    val increase =
        meaningful
            .filter {
                it.differenceMillis > 0L
            }
            .maxByOrNull {
                it.differenceMillis
            }

    return when {
        decrease != null &&
                increase != null ->

            "На этой неделе времени на «${decrease.name}» стало на ${
                shortDuration(
                    abs(
                        decrease.differenceMillis
                    )
                )
            } меньше, а на «${increase.name}» - на ${
                shortDuration(
                    increase.differenceMillis
                )
            } больше."

        decrease != null ->

            "На этой неделе времени на «${decrease.name}» стало на ${
                shortDuration(
                    abs(
                        decrease.differenceMillis
                    )
                )
            } меньше."

        increase != null ->

            "На этой неделе времени на «${increase.name}» стало на ${
                shortDuration(
                    increase.differenceMillis
                )
            } больше."

        else ->
            "Эта неделя пока почти не отличается от прошлой."
    }
}

private fun shortDuration(
    millis: Long
): String {

    val totalMinutes =
        millis / 60_000L

    val hours =
        totalMinutes / 60L

    val minutes =
        totalMinutes % 60L

    return when {
        hours > 0L &&
                minutes > 0L ->
            "${hours} ч ${minutes} мин"

        hours > 0L ->
            "${hours} ч"

        else ->
            "${minutes.coerceAtLeast(1L)} мин"
    }
}

private data class ComparisonBounds(
    val currentStart: Long,
    val currentEnd: Long,
    val previousStart: Long,
    val previousEnd: Long,
)
