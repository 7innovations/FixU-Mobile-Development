package com.example.fixu.database

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    fun saveSession(token: String?, userId: String?, name: String?, email: String?) {
        prefs.edit().apply {
            putString(USER_TOKEN, token)
//            putBoolean(IS_LOGGED_IN, true)
            putString(USER_ID, userId)
            putString(USER_EMAIL, email)
            putString(USER_NAME, name)
            apply()
        }
    }

//    fun isLoggedIn(): Boolean {
//        return prefs.getBoolean(IS_LOGGED_IN, false)
//    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun getUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }

    companion object {
        const val USER_TOKEN = "user_token"
//        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_NAME = "user_name"
        const val USER_ID = "user_id"
        const val USER_EMAIL = "user_email"
    }
}