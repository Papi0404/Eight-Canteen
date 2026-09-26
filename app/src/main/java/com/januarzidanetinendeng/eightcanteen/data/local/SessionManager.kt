package com.januarzidanetinendeng.eightcanteen.data.local

import android.content.Context
import android.content.SharedPreferences
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig

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
        private const val KEY_STUDENT_CLASS = "user_student_class"
        private const val KEY_NIS = "user_nis"
        private const val KEY_STAND_NAME = "user_stand_name"
        private const val KEY_COUNTER_SLOT = "user_counter_slot"

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
        ApiConfig.setAuthToken(token)
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
        points: Int = 0,
        studentClass: String? = null,
        nis: String? = null,
        standName: String? = null,
        counterSlot: String? = null
    ) {
        prefs.edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_STAND_ID, standId)
            putInt(KEY_POINTS, points)
            if (studentClass != null) putString(KEY_STUDENT_CLASS, studentClass)
            if (nis != null) putString(KEY_NIS, nis)
            if (standName != null) putString(KEY_STAND_NAME, standName)
            if (counterSlot != null) putString(KEY_COUNTER_SLOT, counterSlot)
            apply()
        }
    }

    fun updateProfile(name: String, studentClass: String? = null) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            if (studentClass != null) putString(KEY_STUDENT_CLASS, studentClass)
            apply()
        }
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Pengguna") ?: "Pengguna"
    fun getUserRole(): String = prefs.getString(KEY_USER_ROLE, "Siswa") ?: "Siswa"
    fun getUserPhone(): String = prefs.getString(KEY_USER_PHONE, "") ?: ""
    fun getStandId(): String? = prefs.getString(KEY_STAND_ID, null)
    fun getPoints(): Int = prefs.getInt(KEY_POINTS, 0)
    fun getStudentClass(): String? = prefs.getString(KEY_STUDENT_CLASS, null)
    fun getNis(): String? = prefs.getString(KEY_NIS, null)
    fun getStandName(): String? = prefs.getString(KEY_STAND_NAME, null)
    fun getCounterSlot(): String? = prefs.getString(KEY_COUNTER_SLOT, null)

    fun updatePoints(points: Int) {
        prefs.edit().putInt(KEY_POINTS, points).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        ApiConfig.setAuthToken(null)
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }

    fun isProfileComplete(): Boolean {
        val name = getUserName()
        if (name.isBlank() || name.equals("Pengguna", ignoreCase = true) || name.equals("User", ignoreCase = true)) {
            return false
        }
        val role = getUserRole().lowercase()
        if (role == "siswa" || role == "student") {
            val studentClass = getStudentClass()
            return !studentClass.isNullOrBlank()
        }
        return true
    }
}
