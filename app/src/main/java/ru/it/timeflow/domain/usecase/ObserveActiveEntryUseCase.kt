package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class ObserveActiveEntryUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    operator fun invoke() = repository.observeActiveEntry()
}
