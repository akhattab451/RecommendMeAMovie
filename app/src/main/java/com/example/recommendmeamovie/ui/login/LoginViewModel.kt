package com.example.recommendmeamovie.ui.login

import android.net.Uri
import androidx.lifecycle.*
import com.example.recommendmeamovie.repository.LoginRepository
import com.example.recommendmeamovie.util.Constants
import com.example.recommendmeamovie.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _token = MutableLiveData<Resource<String>>()
    val token: LiveData<Resource<String>>
        get() = _token

    fun getAuthUrl(token: String): Uri = Uri.Builder()
        .scheme("https")
        .authority(Constants.AUTH_URL)
        .appendPath("authenticate")
        .appendPath(token)
        .appendQueryParameter(
            "redirect_to",
            Constants.REDIRECT_URI
        ).build()


    fun getRequestToken() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getToken().collect {
                _token.postValue(it)
            }
        }
    }

    companion object {
        const val LOG_TAG = "session-debug"
    }
}