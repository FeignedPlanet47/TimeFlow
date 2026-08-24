package ru.it.timeflow.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun dayBoundsMillis(date: LocalDate = LocalDate.now()): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
    val end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    return start to end
}

fun weekBoundsMillis(date: LocalDate = LocalDate.now()): Pair<Long, Long> {
    val startDate = date.minusDays((date.dayOfWeek.value - 1).toLong())
    val start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val end = startDate.plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return start to end
}

fun monthBoundsMillis(date: LocalDate = LocalDate.now()): Pair<Long, Long> {
    val first = date.withDayOfMonth(1)
    val start = first.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val end = first.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return start to end
}

fun formatDuration(millis: Long): String {
    val totalSeconds = (millis.coerceAtLeast(0L) / 1000)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}

fun formatDurationCompact(millis: Long): String {
    val totalMinutes = millis.coerceAtLeast(0L) / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}ч ${minutes}м"
        hours > 0 -> "${hours}ч"
        else -> "${minutes}м"
    }
}

fun formatClockTime(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))
