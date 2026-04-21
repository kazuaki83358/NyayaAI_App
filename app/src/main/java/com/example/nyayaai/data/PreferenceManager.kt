package com.example.nyayaai.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nyayaai.ui.screens.chat.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val CHAT_MESSAGES = stringPreferencesKey("chat_messages")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROLE = stringPreferencesKey("user_role") // "common_man" or "lawyer"

        // Lawyer Profile Fields
        val LAWYER_NAME = stringPreferencesKey("lawyer_name")
        val LAWYER_EXPERIENCE = stringPreferencesKey("lawyer_experience")
        val LAWYER_SPECIALIZATION = stringPreferencesKey("lawyer_specialization")
        val LAWYER_CITY = stringPreferencesKey("lawyer_city")
        val LAWYER_LANGUAGES = stringPreferencesKey("lawyer_languages")
        val LAWYER_FEE = stringPreferencesKey("lawyer_fee")
        val LAWYER_BAR_NUMBER = stringPreferencesKey("lawyer_bar_number")
        val LAWYER_ABOUT = stringPreferencesKey("lawyer_about")
    }

    private val gson = Gson()

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE] }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = loggedIn }
    }

    suspend fun setUserRole(role: String) {
        context.dataStore.edit { it[USER_ROLE] = role }
    }

    suspend fun saveLawyerProfile(
        name: String,
        experience: String,
        specialization: String,
        city: String,
        languages: String,
        fee: String,
        barNumber: String,
        about: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[LAWYER_NAME] = name
            prefs[LAWYER_EXPERIENCE] = experience
            prefs[LAWYER_SPECIALIZATION] = specialization
            prefs[LAWYER_CITY] = city
            prefs[LAWYER_LANGUAGES] = languages
            prefs[LAWYER_FEE] = fee
            prefs[LAWYER_BAR_NUMBER] = barNumber
            prefs[LAWYER_ABOUT] = about
        }
    }

    val lawyerProfile: Flow<Map<String, String>> = context.dataStore.data.map { prefs ->
        mapOf(
            "name" to (prefs[LAWYER_NAME] ?: ""),
            "experience" to (prefs[LAWYER_EXPERIENCE] ?: ""),
            "specialization" to (prefs[LAWYER_SPECIALIZATION] ?: ""),
            "city" to (prefs[LAWYER_CITY] ?: ""),
            "languages" to (prefs[LAWYER_LANGUAGES] ?: ""),
            "fee" to (prefs[LAWYER_FEE] ?: ""),
            "barNumber" to (prefs[LAWYER_BAR_NUMBER] ?: ""),
            "about" to (prefs[LAWYER_ABOUT] ?: "")
        )
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "en"
    }

    val chatMessages: Flow<List<Message>> = context.dataStore.data.map { preferences ->
        val json = preferences[CHAT_MESSAGES] ?: return@map emptyList<Message>()
        val type = object : TypeToken<List<Message>>() {}.type
        gson.fromJson(json, type)
    }

    suspend fun saveMessages(messages: List<Message>) {
        val json = gson.toJson(messages)
        context.dataStore.edit { preferences ->
            preferences[CHAT_MESSAGES] = json
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }
}