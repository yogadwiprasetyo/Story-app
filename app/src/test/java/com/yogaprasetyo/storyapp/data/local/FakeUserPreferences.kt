package com.yogaprasetyo.storyapp.data.local

import com.yogaprasetyo.storyapp.model.UserDataStoreModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserPreferences :
    UserPreferencesImpl {
    private val tokenKey = "token"
    private val stateLoginKey = "state_login"

    private val dataStore = mutableMapOf<String, Any>(
        tokenKey to "",
        stateLoginKey to false
    )

    override fun loadDatastore(): Flow<UserDataStoreModel> {
        return flowOf(
            UserDataStoreModel(
                token = dataStore[tokenKey] as String,
                isLogin = dataStore[stateLoginKey] as Boolean
            )
        )
    }

    override fun saveUserPreference(userdataStore: UserDataStoreModel) {
        dataStore[tokenKey] = userdataStore.token
        dataStore[stateLoginKey] = userdataStore.isLogin
    }

    override fun removeUserPreference() {
        dataStore[tokenKey] = ""
        dataStore[stateLoginKey] = false
    }
}