package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        name: String,
    ): Long = repository.addTask(categoryId, name)
}
