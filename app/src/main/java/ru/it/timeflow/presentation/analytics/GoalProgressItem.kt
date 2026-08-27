package ru.it.timeflow.presentation.analytics

import ru.it.timeflow.domain.model.ActivityTarget

data class GoalProgressItem(
    val target: ActivityTarget,
    val categoryName: String,
    val categoryEmoji: String,
    val categoryColorArgb: Long,
    val actualMillis: Long,
    val progressPercent: Float,
) {
    val remainingMillis: Long
        get() =
            (
                    target.targetMillis -
                            actualMillis
                    ).coerceAtLeast(0L)

    val exceededMillis: Long
        get() =
            (
                    actualMillis -
                            target.targetMillis
                    ).coerceAtLeast(0L)

    val isReached: Boolean
        get() =
            actualMillis >=
                    target.targetMillis

    val progressFraction: Float
        get() =
            (
                    progressPercent /
                            100f
                    ).coerceIn(
                    0f,
                    1f,
                )
}
