package com.yogaprasetyo.storyapp.model

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.maps.model.LatLng
import com.yogaprasetyo.storyapp.data.UserRepository
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.data.remote.response.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

/**
 * Handle data from data layer to UI layer
 * Gate for connect to source data
 * */
class UserViewModel(private val userRepo: UserRepository) : ViewModel() {

    private var _resLogin = MutableLiveData<ResponseLogin>()
    val responseLogin: LiveData<ResponseLogin> = _resLogin

    private var _resRegister = MutableLiveData<ResponseRegister>()
    val responseRegister: LiveData<ResponseRegister> = _resRegister

    private var _resStory = MutableLiveData<ResponseUploadStory>()
    val responseStory: LiveData<ResponseUploadStory> = _resStory

    fun register(user: UserRegisterModel) {
        viewModelScope.launch {
            userRepo.createUser(user).collect { response ->
                _resRegister.value = response
            }
        }
    }

    fun login(user: UserLoginModel, pref: UserPreferences) {
        viewModelScope.launch {
            userRepo.authenticate(user, pref).collect { response ->
                _resLogin.value = response
            }
        }
    }

    fun logout(pref: UserPreferences) {
        viewModelScope.launch {
            pref.removeUserPreference()
        }
    }

    fun loadPreferences(pref: UserPreferences): LiveData<UserDataStoreModel> {
        return pref.loadDataStore().asLiveData()
    }

    fun loadAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return userRepo.getAllStories(token).cachedIn(viewModelScope)
    }

    fun loadAllStoriesWithLocation(token: String): LiveData<ResponseStories> {
        return userRepo.getAllStoriesWithLocation(token)
    }

    fun uploadStory(token: String, file: File, description: String, location: LatLng) {
        viewModelScope.launch {
            userRepo.uploadStory(token, file, description, location).collect { response ->
                _resStory.value = response
            }
        }
    }
}