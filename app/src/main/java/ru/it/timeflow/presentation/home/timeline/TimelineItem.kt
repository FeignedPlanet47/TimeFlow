package ru.it.timeflow.presentation.home.timeline

import ru.it.timeflow.domain.model.TimeEntry
import kotlin.math.max
import kotlin.math.min

sealed interface TimelineItem {

    val startMillis: Long
    val endMillis: Long

    data class Entry(
        val entry: TimeEntry,
        override val startMillis: Long,
        override val endMillis: Long,
    ) : TimelineItem

    data class Gap(
        override val startMillis: Long,
        override val endMillis: Long,
    ) : TimelineItem {
        val durationMillis: Long
            get() = (endMillis - startMillis).coerceAtLeast(0L)
    }
}

fun buildTodayTimeline(
    entries: List<TimeEntry>,
    dayStartMillis: Long,
    nowMillis: Long,
    minimumGapMillis: Long = 120_000L,
): List<TimelineItem> {

    val timelineEnd =
        max(dayStartMillis, nowMillis)

    val normalizedEntries =
        entries
            .sortedBy { it.startTimeMillis }
            .mapNotNull { entry ->

                val start =
                    max(
                        dayStartMillis,
                        entry.startTimeMillis,
                    )

                val rawEnd =
                    entry.endTimeMillis
                        ?: nowMillis

                val end =
                    min(
                        timelineEnd,
                        rawEnd,
                    )

                if (end <= start) {
                    null
                } else {
                    TimelineItem.Entry(
                        entry = entry,
                        startMillis = start,
                        endMillis = end,
                    )
                }
            }

    if (normalizedEntries.isEmpty()) {

        val duration =
            timelineEnd - dayStartMillis

        return if (
            duration >= minimumGapMillis
        ) {
            listOf(
                TimelineItem.Gap(
                    startMillis =
                        dayStartMillis,
                    endMillis =
                        timelineEnd,
                )
            )
        } else {
            emptyList()
        }
    }

    val result =
        mutableListOf<TimelineItem>()

    var cursor =
        dayStartMillis

    normalizedEntries.forEach { item ->

        if (
            item.startMillis - cursor >=
            minimumGapMillis
        ) {
            result +=
                TimelineItem.Gap(
                    startMillis = cursor,
                    endMillis =
                        item.startMillis,
                )
        }

        result += item

        cursor =
            max(
                cursor,
                item.endMillis,
            )
    }

    if (
        timelineEnd - cursor >=
        minimumGapMillis
    ) {
        result +=
            TimelineItem.Gap(
                startMillis = cursor,
                endMillis = timelineEnd,
            )
    }

    return result
}
