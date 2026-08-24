package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class ObserveCategoriesUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    operator fun invoke() = repository.observeCategories()
}
