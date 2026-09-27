package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String, // "تفصيل", "تفضيل", "تعليمات", "مشروع"
    val content: String,
    val isSensitive: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
