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
    }

    private val gson = Gson()

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