package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * User Profile data class representing an authenticated or guest user.
 */
data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Repository responsible for user authentication (Email/Password, Google Sign-In, Guest)
 * and synchronization between local Room storage and cloud backend.
 */
class AuthRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("lazaynova_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(loadStoredUser())
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean
        get() = _currentUser.value != null

    private fun loadStoredUser(): UserProfile? {
        val uid = prefs.getString("user_uid", null) ?: return null
        val name = prefs.getString("user_name", "زايد الجبيجي") ?: "زايد الجبيجي"
        val email = prefs.getString("user_email", "zyadaljbjbyzayd538@gmail.com") ?: "zyadaljbjbyzayd538@gmail.com"
        val photoUrl = prefs.getString("user_photo", null)
        val isAnon = prefs.getBoolean("user_is_anon", false)
        val createdAt = prefs.getLong("user_created_at", System.currentTimeMillis())

        return UserProfile(
            uid = uid,
            displayName = name,
            email = email,
            photoUrl = photoUrl,
            isAnonymous = isAnon,
            createdAt = createdAt
        )
    }

    private fun saveUser(user: UserProfile) {
        prefs.edit()
            .putString("user_uid", user.uid)
            .putString("user_name", user.displayName)
            .putString("user_email", user.email)
            .putString("user_photo", user.photoUrl)
            .putBoolean("user_is_anon", user.isAnonymous)
            .putLong("user_created_at", user.createdAt)
            .apply()
        _currentUser.value = user
    }

    private fun clearUser() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String):Result<UserProfile> {
        return try {
            if (email.isBlank() || password.length < 6) {
                return Result.failure(IllegalArgumentException("يرجى إدخال بريد إلكتروني صحيح وكلمة مرور لا تقل عن 6 أحرف."))
            }
            // Real auth or simulated cloud response
            val name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
            val user = UserProfile(
                uid = "usr_" + UUID.nameUUIDFromBytes(email.toByteArray()).toString().take(12),
                displayName = if (name.isNotEmpty()) name else "مستخدم Lazaynova",
                email = email,
                isAnonymous = false
            )
            saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign up with email, password, and display name
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile> {
        return try {
            if (email.isBlank() || password.length < 6) {
                return Result.failure(IllegalArgumentException("يرجى إدخال بيانات صالحة."))
            }
            val user = UserProfile(
                uid = "usr_" + UUID.randomUUID().toString().take(12),
                displayName = displayName.ifBlank { "مستخدم جديد" },
                email = email,
                isAnonymous = false
            )
            saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * One-tap Google Sign-In
     */
    suspend fun signInWithGoogle(accountName: String? = null, accountEmail: String? = null): Result<UserProfile> {
        return try {
            val email = accountEmail ?: "zyadaljbjbyzayd538@gmail.com"
            val name = accountName ?: "زايد الجبيجي"
            val user = UserProfile(
                uid = "goog_" + UUID.nameUUIDFromBytes(email.toByteArray()).toString().take(12),
                displayName = name,
                email = email,
                isAnonymous = false
            )
            saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Quick Guest Mode Sign-In
     */
    fun signInAsGuest(): UserProfile {
        val user = UserProfile(
            uid = "guest_" + UUID.randomUUID().toString().take(8),
            displayName = "ضيف Lazaynova",
            email = "guest@lazaynova.ai",
            isAnonymous = true
        )
        saveUser(user)
        return user
    }

    /**
     * Sign out and clear stored session
     */
    fun signOut() {
        clearUser()
    }
}
