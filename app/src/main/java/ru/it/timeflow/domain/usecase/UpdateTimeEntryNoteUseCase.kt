package ru.it.timeflow.domain.usecase

import ru.it.timeflow.domain.repository.TimeTrackerRepository
import javax.inject.Inject

class UpdateTimeEntryNoteUseCase @Inject constructor(
    private val repository: TimeTrackerRepository,
) {
    suspend operator fun invoke(
        entryId: Long,
        note: String?,
    ) {
        repository.updateEntryNote(
            entryId = entryId,
            note = note,
        )
    }
}
