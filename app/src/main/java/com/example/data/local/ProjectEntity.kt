package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val projectType: String = "Android", // Android, Python, Web, Script
    val fileCount: Int = 1,
    val status: String = "Active",
    val updatedAt: Long = System.currentTimeMillis()
)
