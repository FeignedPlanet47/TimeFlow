package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.model.ActivityTarget
import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class SaveActivityTargetUseCase @Inject constructor(
    private val repository:
    TimeTrackerRepository,
) {
    suspend operator fun invoke(
        target: ActivityTarget,
    ) {
        require(
            target.targetMillis > 0L
        ) {
            "Target duration must be greater than zero"
        }

        repository.saveActivityTarget(
            target
        )
    }
}
