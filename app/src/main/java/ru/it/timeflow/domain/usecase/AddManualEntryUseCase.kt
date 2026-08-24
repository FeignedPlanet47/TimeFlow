package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class AddManualEntryUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        startMillis: Long,
        endMillis: Long,
        taskName: String? = null,
        note: String? = null,
    ) = repository.addManualEntry(categoryId, startMillis, endMillis, taskName, note)
}
