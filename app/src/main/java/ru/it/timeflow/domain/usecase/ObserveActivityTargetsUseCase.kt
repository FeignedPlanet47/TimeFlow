package ru.it.timeflow.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class ObserveActivityTargetsUseCase @Inject constructor(
    private val repository:
    TimeTrackerRepository,
) {
    operator fun invoke():
            Flow<List<ActivityTarget>> =
        repository.observeActivityTargets()
}
