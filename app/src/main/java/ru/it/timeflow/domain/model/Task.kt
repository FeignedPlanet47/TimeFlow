package ru.it.timeflow.domain.model

data class Task(
    val id: Long,
    val categoryId: Long,
    val name: String
)
