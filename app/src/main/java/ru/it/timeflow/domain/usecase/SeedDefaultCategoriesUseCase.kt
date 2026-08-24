package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class SeedDefaultCategoriesUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke() = repository.seedDefaults()
}
