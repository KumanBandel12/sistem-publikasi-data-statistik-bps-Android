package com.bps.publikasistatistik.presentation.profile.change_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.user.ChangePasswordUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ChangePasswordUiState())
    val state: State<ChangePasswordUiState> = _state

    fun onEvent(event: ChangePasswordEvent) {
        when(event) {
            is ChangePasswordEvent.EnteredOldPassword -> {
                _state.value = _state.value.copy(oldPassword = event.value)
            }
            is ChangePasswordEvent.EnteredNewPassword -> {
                _state.value = _state.value.copy(newPassword = event.value)
            }
            is ChangePasswordEvent.EnteredConfirmPassword -> {
                _state.value = _state.value.copy(confirmPassword = event.value)
            }
        }
    }

    fun changePassword() {
        val currentState = _state.value

        // 1. Validasi Input di Sisi Client
        if (currentState.newPassword != currentState.confirmPassword) {
            _state.value = _state.value.copy(error = "Konfirmasi password tidak cocok")
            return
        }

        if (currentState.newPassword.length < 8) {
            _state.value = _state.value.copy(error = "Password minimal 8 karakter")
            return
        }

        // 2. Kirim ke Backend
        viewModelScope.launch {
            changePasswordUseCase(currentState.oldPassword, currentState.newPassword).collect { result ->
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

sealed class ChangePasswordEvent {
    data class EnteredOldPassword(val value: String) : ChangePasswordEvent()
    data class EnteredNewPassword(val value: String) : ChangePasswordEvent()
    data class EnteredConfirmPassword(val value: String) : ChangePasswordEvent()
}