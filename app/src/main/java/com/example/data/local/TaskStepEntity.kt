package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_steps")
data class TaskStepEntity(
    @PrimaryKey
    val id: String,
    val messageId: String,
    val stepIndex: Int,
    val title: String,
    val status: String = "pending", // pending, running, completed, failed
    val logOutput: String? = null
)
