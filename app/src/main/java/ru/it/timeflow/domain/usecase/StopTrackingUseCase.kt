package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke() = repository.stopTracking()
}
