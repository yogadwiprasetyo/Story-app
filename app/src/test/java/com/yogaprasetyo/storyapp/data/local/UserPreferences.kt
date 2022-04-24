package com.yogaprasetyo.storyapp.data.local

import com.yogaprasetyo.storyapp.model.UserDataStoreModel
import kotlinx.coroutines.flow.Flow

interface UserPreferencesImpl {
    fun loadDatastore(): Flow<UserDataStoreModel>
    fun saveUserPreference(userdataStore: UserDataStoreModel)
    fun removeUserPreference()
}