package com.bps.publikasistatistik.presentation.auth.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.auth.RegisterUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RegisterUiState())
    val state: State<RegisterUiState> = _state

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.EnteredUsername -> _state.value = _state.value.copy(username = event.value)
            is RegisterEvent.EnteredEmail -> _state.value = _state.value.copy(email = event.value)
            is RegisterEvent.EnteredPassword -> _state.value = _state.value.copy(password = event.value)
            is RegisterEvent.EnteredConfirmPassword -> _state.value = _state.value.copy(confirmPassword = event.value)
        }
    }

    fun register() {
        val currentState = _state.value

        // 1. Validasi Client Side
        if (currentState.password != currentState.confirmPassword) {
            _state.value = _state.value.copy(error = "Konfirmasi password tidak cocok")
            return
        }
        if (currentState.password.length < 8) {
            _state.value = _state.value.copy(error = "Password minimal 8 karakter")
            return
        }

        // 2. Kirim ke Backend
        viewModelScope.launch {
            registerUseCase(currentState.username, currentState.email, currentState.password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}

sealed class RegisterEvent {
    data class EnteredUsername(val value: String) : RegisterEvent()
    data class EnteredEmail(val value: String) : RegisterEvent()
    data class EnteredPassword(val value: String) : RegisterEvent()
    data class EnteredConfirmPassword(val value: String) : RegisterEvent()
}