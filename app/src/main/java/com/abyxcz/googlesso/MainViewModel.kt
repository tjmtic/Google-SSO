package com.abyxcz.googlesso

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    private val _state = MutableStateFlow(MainState())
    val state : StateFlow<MainState> = _state

    init {
        auth.currentUser?.let {
            setUser(it)
        }
    }


    private fun onEvent(event: MainViewEvent) {
        when (event) {
            is MainViewEvent.AuthChanged -> {
                _state.update{ it.copy(user = event.user)}
            }
        }
    }

    fun setUser(user: FirebaseUser){
        onEvent(MainViewEvent.AuthChanged(user))
    }

    data class MainState(
        val user: FirebaseUser? = null
    )

    sealed class MainUiState {
        object Default: MainUiState()
        object Loading: MainUiState()
        object Success: MainUiState()
        data class Error(val exception: Throwable): MainUiState()
    }

    sealed class MainViewEvent {
        data class AuthChanged(val user: FirebaseUser?) : MainViewEvent()
    }
}