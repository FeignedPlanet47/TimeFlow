package ru.it.timeflow.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.it.timeflow.domain.model.Task
import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class ObserveTasksByCategoryUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    operator fun invoke(categoryId: Long): Flow<List<Task>> =
        repository.observeTasksByCategory(categoryId)
}
