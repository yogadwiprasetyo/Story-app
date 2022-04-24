package com.yogaprasetyo.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yogaprasetyo.storyapp.model.UserDataStoreModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Handle DataStore to save data on local
 * dataStore: token and state login
 * */
class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    /**
     * Get datastore value
     * */
    fun loadDataStore(): Flow<UserDataStoreModel> {
        return dataStore.data.map { preferences ->
            UserDataStoreModel(
                preferences[tokenKey] ?: "",
                preferences[stateLoginKey] ?: false
            )
        }
    }

    /**
     * Save datastore value
     * This use for verified user is login or not
     * */
    suspend fun saveUserPreference(userDataStore: UserDataStoreModel) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = userDataStore.token
            preferences[stateLoginKey] = userDataStore.isLogin
        }
    }

    /**
     * Remove datastore value
     * This use to set user is logout
     * */
    suspend fun removeUserPreference() {
        dataStore.edit { preferences ->
            preferences[tokenKey] = ""
            preferences[stateLoginKey] = false
        }
    }

    /**
     * Singleton pattern, only create once
     * */
    companion object {
        @Volatile
        private var instance: UserPreferences? = null

        private val tokenKey = stringPreferencesKey("token")
        private val stateLoginKey = booleanPreferencesKey("state_login")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return instance ?: synchronized(this) {
                val userPreference = UserPreferences(dataStore)
                instance = userPreference
                userPreference
            }
        }
    }
}