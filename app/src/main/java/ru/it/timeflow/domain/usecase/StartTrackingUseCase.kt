package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class StartTrackingUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        taskId: Long? = null,
        taskName: String? = null,
        note: String? = null,
    ) = repository.startTracking(
        categoryId = categoryId,
        taskId = taskId,
        taskName = taskName,
        note = note,
    )
}
