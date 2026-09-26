package com.januarzidanetinendeng.eightcanteen.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "eight_canteen_session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_STAND_ID = "user_stand_id"
        private const val KEY_POINTS = "user_points"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUser(
        id: String,
        name: String,
        role: String,
        phone: String,
        standId: String? = null,
        points: Int = 0
    ) {
        prefs.edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_STAND_ID, standId)
            putInt(KEY_POINTS, points)
            apply()
        }
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Pengguna") ?: "Pengguna"
    fun getUserRole(): String = prefs.getString(KEY_USER_ROLE, "Siswa") ?: "Siswa"
    fun getUserPhone(): String = prefs.getString(KEY_USER_PHONE, "") ?: ""
    fun getStandId(): String? = prefs.getString(KEY_STAND_ID, null)
    fun getPoints(): Int = prefs.getInt(KEY_POINTS, 0)

    fun updatePoints(points: Int) {
        prefs.edit().putInt(KEY_POINTS, points).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }
}
