package ru.it.timeflow.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.model.ActivityTargetPeriod
import ru.it.timeflow.domain.model.ActivityTargetType
import ru.it.timeflow.domain.model.Category
import ru.it.timeflow.domain.model.TimeEntry
import ru.it.timeflow.domain.usecase.DeleteActivityTargetUseCase
import ru.it.timeflow.domain.usecase.ObserveActivityTargetsUseCase
import ru.it.timeflow.domain.usecase.ObserveCategoriesUseCase
import ru.it.timeflow.domain.usecase.ObserveEntriesBetweenUseCase
import ru.it.timeflow.domain.usecase.SaveActivityTargetUseCase
import ru.it.timeflow.util.dayBoundsMillis
import ru.it.timeflow.util.monthBoundsMillis
import ru.it.timeflow.util.weekBoundsMillis
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    observeEntriesBetween:
    ObserveEntriesBetweenUseCase,

    observeCategories:
    ObserveCategoriesUseCase,

    observeActivityTargets:
    ObserveActivityTargetsUseCase,

    private val saveActivityTarget:
    SaveActivityTargetUseCase,

    private val deleteActivityTarget:
    DeleteActivityTargetUseCase,
) : ViewModel() {

    private val selectedPeriod =
        MutableStateFlow(
            AnalyticsPeriod.DAY
        )

    private val nowMillis =
        MutableStateFlow(
            System.currentTimeMillis()
        )

    private val targetEditorRequest =
        MutableStateFlow<
                TargetEditorRequest?
                >(null)

    private val periodEntries =
        selectedPeriod
            .flatMapLatest { period ->

                val bounds =
                    period.bounds()

                observeEntriesBetween(
                    startMillis =
                        bounds.first,
                    endMillis =
                        bounds.second,
                )
            }

    private val longRangeQueryStart =
        minOf(
            System.currentTimeMillis() -
                    THIRTY_DAYS_MILLIS,

            weekComparisonQueryStart(
                System.currentTimeMillis()
            )
        )

    private val longRangeEntries =
        observeEntriesBetween(
            startMillis =
                longRangeQueryStart,
            endMillis =
                Long.MAX_VALUE,
        )

    private val targetWindowStart =
        nowMillis
            .map { now ->
                earliestTargetPeriodStart(
                    now
                )
            }
            .distinctUntilChanged()

    private val targetEntries =
        targetWindowStart
            .flatMapLatest { start ->

                observeEntriesBetween(
                    startMillis =
                        start,
                    endMillis =
                        Long.MAX_VALUE,
                )
            }

    private val targetData =
        combine(
            observeActivityTargets(),
            observeCategories(),
            targetEntries,
        ) {
                targets,
                categories,
                entries ->

            TargetData(
                targets =
                    targets,
                categories =
                    categories,
                entries =
                    entries,
            )
        }

    private val analyticsData =
        combine(
            periodEntries,
            longRangeEntries,
            targetData,
        ) {
                periodEntries,
                longRangeEntries,
                targetData ->

            AnalyticsData(
                periodEntries =
                    periodEntries,
                longRangeEntries =
                    longRangeEntries,
                targetData =
                    targetData,
            )
        }

    private val contentState =
        combine(
            analyticsData,
            selectedPeriod,
            nowMillis,
        ) {
                analyticsData,
                period,
                now ->

            buildState(
                periodEntries =
                    analyticsData
                        .periodEntries,

                longRangeEntries =
                    analyticsData
                        .longRangeEntries,

                targets =
                    analyticsData
                        .targetData
                        .targets,

                categories =
                    analyticsData
                        .targetData
                        .categories,

                targetEntries =
                    analyticsData
                        .targetData
                        .entries,

                period =
                    period,

                nowMillis =
                    now,
            )
        }

    val state =
        combine(
            contentState,
            targetEditorRequest,
        ) {
                content,
                editor ->

            content.copy(
                isTargetEditorOpen =
                    editor != null,

                editingTarget =
                    editor?.target,
            )
        }
            .stateIn(
                scope =
                    viewModelScope,

                started =
                    SharingStarted
                        .WhileSubscribed(
                            5_000
                        ),

                initialValue =
                    AnalyticsUiState(),
            )

    init {
        viewModelScope.launch {

            while (isActive) {

                nowMillis.value =
                    System
                        .currentTimeMillis()

                delay(1_000)
            }
        }
    }

    fun selectPeriod(
        period: AnalyticsPeriod,
    ) {
        selectedPeriod.value =
            period
    }

    fun openCreateTarget() {
        targetEditorRequest.value =
            TargetEditorRequest(
                target = null
            )
    }

    fun openEditTarget(
        target: ActivityTarget,
    ) {
        targetEditorRequest.value =
            TargetEditorRequest(
                target =
                    target
            )
    }

    fun closeTargetEditor() {
        targetEditorRequest.value =
            null
    }

    fun saveTarget(
        categoryId: Long,
        type:
        ActivityTargetType,
        period:
        ActivityTargetPeriod,
        targetMillis: Long,
    ) {
        if (
            targetMillis <= 0L
        ) {
            return
        }

        viewModelScope.launch {

            saveActivityTarget(
                ActivityTarget(
                    categoryId =
                        categoryId,
                    type =
                        type,
                    period =
                        period,
                    targetMillis =
                        targetMillis,
                )
            )

            closeTargetEditor()
        }
    }

    fun deleteTarget(
        categoryId: Long,
    ) {
        viewModelScope.launch {

            deleteActivityTarget(
                categoryId
            )

            closeTargetEditor()
        }
    }

    private fun buildState(
        periodEntries:
        List<TimeEntry>,

        longRangeEntries:
        List<TimeEntry>,

        targets:
        List<ActivityTarget>,

        categories:
        List<Category>,

        targetEntries:
        List<TimeEntry>,

        period:
        AnalyticsPeriod,

        nowMillis:
        Long,
    ): AnalyticsUiState {

        val bounds =
            period.bounds()

        val periodStart =
            bounds.first

        val periodEnd =
            min(
                bounds.second,
                nowMillis,
            )

        val periodSummaries =
            buildCategorySummaries(
                entries =
                    periodEntries,

                periodStart =
                    periodStart,

                periodEnd =
                    periodEnd,

                nowMillis =
                    nowMillis,
            )

        val totalMillis =
            periodSummaries
                .sumOf {
                    it.durationMillis
                }

        val items =
            periodSummaries
                .map { summary ->

                    val percentage =
                        if (
                            totalMillis > 0L
                        ) {
                            (
                                    summary
                                        .durationMillis
                                        .toDouble() /
                                            totalMillis
                                                .toDouble() *
                                            100.0
                                    ).toFloat()
                        } else {
                            0f
                        }

                    AnalyticsCategoryItem(
                        categoryId =
                            summary.categoryId,

                        name =
                            summary.name,

                        emoji =
                            summary.emoji,

                        colorArgb =
                            summary.colorArgb,

                        durationMillis =
                            summary.durationMillis,

                        percentage =
                            percentage,
                    )
                }

        val lifeTimeStart =
            nowMillis -
                    THIRTY_DAYS_MILLIS

        val lifeTimeSummaries =
            buildCategorySummaries(
                entries =
                    longRangeEntries,

                periodStart =
                    lifeTimeStart,

                periodEnd =
                    nowMillis,

                nowMillis =
                    nowMillis,
            )

        val lifeTimeItems =
            lifeTimeSummaries
                .map { summary ->

                    LifeTimeItem(
                        categoryId =
                            summary.categoryId,

                        name =
                            summary.name,

                        emoji =
                            summary.emoji,

                        colorArgb =
                            summary.colorArgb,

                        last30DaysMillis =
                            summary.durationMillis,

                        projectedYearDays =
                            projectedYearDays(
                                summary
                                    .durationMillis
                            ),
                    )
                }
                .sortedByDescending {
                    it.last30DaysMillis
                }

        val goalProgress =
            buildGoalProgress(
                targets =
                    targets,

                categories =
                    categories,

                entries =
                    targetEntries,

                nowMillis =
                    nowMillis,
            )

        val weekComparison =
            buildWeekComparison(
                entries =
                    longRangeEntries,

                nowMillis =
                    nowMillis,
            )

        return AnalyticsUiState(
            selectedPeriod =
                period,

            items =
                items,

            totalMillis =
                totalMillis,

            periodTitle =
                periodTitle(
                    period =
                        period,

                    nowMillis =
                        nowMillis,
                ),

            weekComparisonItems =
                weekComparison.items,

            weekComparisonSummary =
                weekComparison.summary,

            lifeTimeItems =
                lifeTimeItems,

            lifeTimeTotalMillis =
                lifeTimeItems.sumOf {
                    it.last30DaysMillis
                },

            categories =
                categories,

            goalProgressItems =
                goalProgress,

            isLoading =
                false,
        )
    }

    private fun buildGoalProgress(
        targets:
        List<ActivityTarget>,

        categories:
        List<Category>,

        entries:
        List<TimeEntry>,

        nowMillis:
        Long,
    ): List<GoalProgressItem> {

        return targets
            .mapNotNull { target ->

                val category =
                    categories
                        .firstOrNull {
                            it.id ==
                                    target
                                        .categoryId
                        }
                        ?: return@mapNotNull null

                val bounds =
                    targetBounds(
                        period =
                            target.period,

                        nowMillis =
                            nowMillis,
                    )

                val periodEnd =
                    min(
                        bounds.second,
                        nowMillis,
                    )

                val actualMillis =
                    entries
                        .asSequence()
                        .filter {
                            it.categoryId ==
                                    target
                                        .categoryId
                        }
                        .sumOf { entry ->

                            overlapDuration(
                                entry =
                                    entry,

                                periodStart =
                                    bounds.first,

                                periodEnd =
                                    periodEnd,

                                nowMillis =
                                    nowMillis,
                            )
                        }

                val progress =
                    if (
                        target
                            .targetMillis >
                        0L
                    ) {
                        (
                                actualMillis
                                    .toDouble() /
                                        target
                                            .targetMillis
                                            .toDouble() *
                                        100.0
                                ).toFloat()
                    } else {
                        0f
                    }

                GoalProgressItem(
                    target =
                        target,

                    categoryName =
                        category.name,

                    categoryEmoji =
                        category.emoji,

                    categoryColorArgb =
                        category.colorArgb,

                    actualMillis =
                        actualMillis,

                    progressPercent =
                        progress,
                )
            }
            .sortedWith(
                compareBy<
                        GoalProgressItem
                        > {
                    it.target.type
                }
                    .thenBy {
                        it.categoryName
                    }
            )
    }

    private fun targetBounds(
        period:
        ActivityTargetPeriod,

        nowMillis:
        Long,
    ): Pair<Long, Long> {

        val date =
            Instant
                .ofEpochMilli(
                    nowMillis
                )
                .atZone(
                    ZoneId
                        .systemDefault()
                )
                .toLocalDate()

        return when (period) {

            ActivityTargetPeriod.DAY ->
                dayBoundsMillis(
                    date
                )

            ActivityTargetPeriod.WEEK ->
                weekBoundsMillis(
                    date
                )

            ActivityTargetPeriod.MONTH ->
                monthBoundsMillis(
                    date
                )
        }
    }

    private fun earliestTargetPeriodStart(
        nowMillis:
        Long,
    ): Long {

        val date =
            Instant
                .ofEpochMilli(
                    nowMillis
                )
                .atZone(
                    ZoneId
                        .systemDefault()
                )
                .toLocalDate()

        return minOf(
            dayBoundsMillis(
                date
            ).first,

            weekBoundsMillis(
                date
            ).first,

            monthBoundsMillis(
                date
            ).first,
        )
    }

    private fun buildCategorySummaries(
        entries:
        List<TimeEntry>,

        periodStart:
        Long,

        periodEnd:
        Long,

        nowMillis:
        Long,
    ): List<RawCategorySummary> {

        return entries
            .groupBy {
                it.categoryId
            }
            .mapNotNull {
                    (_, group) ->

                val first =
                    group
                        .firstOrNull()
                        ?: return@mapNotNull null

                val duration =
                    group.sumOf {
                            entry ->

                        overlapDuration(
                            entry =
                                entry,

                            periodStart =
                                periodStart,

                            periodEnd =
                                periodEnd,

                            nowMillis =
                                nowMillis,
                        )
                    }

                if (
                    duration <= 0L
                ) {
                    null
                } else {
                    RawCategorySummary(
                        categoryId =
                            first.categoryId,

                        name =
                            first.categoryName,

                        emoji =
                            first.categoryEmoji,

                        colorArgb =
                            first.categoryColorArgb,

                        durationMillis =
                            duration,
                    )
                }
            }
            .sortedByDescending {
                it.durationMillis
            }
    }

    private fun projectedYearDays(
        last30DaysMillis:
        Long,
    ): Double {

        if (
            last30DaysMillis <=
            0L
        ) {
            return 0.0
        }

        val yearlyMillis =
            last30DaysMillis
                .toDouble() *
                    DAYS_IN_YEAR /
                    SAMPLE_DAYS

        return yearlyMillis /
                MILLIS_PER_DAY
    }

    private fun overlapDuration(
        entry:
        TimeEntry,

        periodStart:
        Long,

        periodEnd:
        Long,

        nowMillis:
        Long,
    ): Long {

        val entryStart =
            max(
                entry.startTimeMillis,
                periodStart,
            )

        val entryEnd =
            min(
                entry.endTimeMillis
                    ?: nowMillis,
                periodEnd,
            )

        return (
                entryEnd -
                        entryStart
                ).coerceAtLeast(
                0L
            )
    }

    private fun periodTitle(
        period:
        AnalyticsPeriod,

        nowMillis:
        Long,
    ): String {

        val zone =
            ZoneId.systemDefault()

        val date =
            Instant
                .ofEpochMilli(
                    nowMillis
                )
                .atZone(zone)
                .toLocalDate()

        return when (period) {

            AnalyticsPeriod.DAY ->
                "Сегодня"

            AnalyticsPeriod.WEEK ->
                weekTitle(
                    date
                )

            AnalyticsPeriod.MONTH ->
                monthTitle(
                    date
                )
        }
    }

    private fun weekTitle(
        date:
        LocalDate,
    ): String {

        val start =
            date.minusDays(
                (
                        date
                            .dayOfWeek
                            .value -
                                1
                        ).toLong()
            )

        val end =
            start.plusDays(6)

        val formatter =
            DateTimeFormatter
                .ofPattern(
                    "d MMM",
                    Locale("ru"),
                )

        return "${
            start.format(formatter)
        } - ${
            end.format(formatter)
        }"
    }

    private fun monthTitle(
        date:
        LocalDate,
    ): String {

        val formatter =
            DateTimeFormatter
                .ofPattern(
                    "LLLL yyyy",
                    Locale("ru"),
                )

        return date
            .format(formatter)
            .replaceFirstChar {
                if (
                    it.isLowerCase()
                ) {
                    it.titlecase()
                } else {
                    it.toString()
                }
            }
    }

    private data class RawCategorySummary(
        val categoryId:
        Long,

        val name:
        String,

        val emoji:
        String,

        val colorArgb:
        Long,

        val durationMillis:
        Long,
    )

    private data class TargetData(
        val targets:
        List<ActivityTarget>,

        val categories:
        List<Category>,

        val entries:
        List<TimeEntry>,
    )

    private data class AnalyticsData(
        val periodEntries:
        List<TimeEntry>,

        val longRangeEntries:
        List<TimeEntry>,

        val targetData:
        TargetData,
    )

    private data class TargetEditorRequest(
        val target:
        ActivityTarget?,
    )

    companion object {

        private const val
                DAYS_IN_YEAR =
            365.0

        private const val
                SAMPLE_DAYS =
            30.0

        private const val
                MILLIS_PER_DAY =
            86_400_000.0

        private const val
                THIRTY_DAYS_MILLIS =
            30L *
                    24L *
                    60L *
                    60L *
                    1_000L
    }
}
