package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val role: String, // "user", "model", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolType: String = "general", // general, code, task, image, video, music, research, guided_learning
    val taskStatus: String = "idle", // idle, planning, executing, verified, error
    val codeSnippet: String? = null,
    val executionLogs: String? = null,
    val thinkingProcess: String? = null,
    val reactions: String = "" // Comma-separated emojis e.g. "❤️,👍,🔥"
) {
    fun getReactionList(): List<String> {
        return if (reactions.isBlank()) emptyList() else reactions.split(",").filter { it.isNotBlank() }
    }
}
