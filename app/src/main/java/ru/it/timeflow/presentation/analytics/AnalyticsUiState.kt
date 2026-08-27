package ru.it.timeflow.presentation.analytics

import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.model.Category

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsPeriod =
        AnalyticsPeriod.DAY,
    val items:
    List<AnalyticsCategoryItem> =
        emptyList(),
    val totalMillis: Long = 0L,
    val periodTitle: String =
        "Сегодня",
    val lifeTimeItems:
    List<LifeTimeItem> =
        emptyList(),
    val lifeTimeTotalMillis:
    Long = 0L,
    val categories:
    List<Category> =
        emptyList(),
    val goalProgressItems:
    List<GoalProgressItem> =
        emptyList(),
    val isTargetEditorOpen:
    Boolean = false,
    val editingTarget:
    ActivityTarget? = null,
    val isLoading: Boolean = true,
) {
    val canAddTarget: Boolean
        get() =
            categories.any {
                    category ->
                goalProgressItems.none {
                        item ->
                    item.target.categoryId ==
                            category.id
                }
            }
}
