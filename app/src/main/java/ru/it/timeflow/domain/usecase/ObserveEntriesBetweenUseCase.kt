package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class ObserveEntriesBetweenUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    operator fun invoke(startMillis: Long, endMillis: Long) =
        repository.observeEntriesBetween(startMillis, endMillis)
}
