package com.bps.publikasistatistik.presentation.auth.forgot_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.auth.ForgotPasswordUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ForgotPasswordUiState())
    val state: State<ForgotPasswordUiState> = _state

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onDateOfBirthChange(dateOfBirth: String) {
        _state.value = _state.value.copy(dateOfBirth = dateOfBirth)
    }

    fun onPlaceOfBirthChange(placeOfBirth: String) {
        _state.value = _state.value.copy(placeOfBirth = placeOfBirth)
    }

    fun onNewPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(newPassword = newPassword)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword)
    }

    fun submit() {
        val email = _state.value.email
        val dateOfBirth = _state.value.dateOfBirth
        val placeOfBirth = _state.value.placeOfBirth
        val newPassword = _state.value.newPassword
        val confirmPassword = _state.value.confirmPassword

        // Validasi
        if (email.isBlank()) {
            _state.value = _state.value.copy(error = "Email wajib diisi")
            return
        }
        if (dateOfBirth.isBlank()) {
            _state.value = _state.value.copy(error = "Tanggal lahir wajib diisi")
            return
        }
        if (placeOfBirth.isBlank()) {
            _state.value = _state.value.copy(error = "Tempat lahir wajib diisi")
            return
        }
        if (newPassword.isBlank()) {
            _state.value = _state.value.copy(error = "Password baru wajib diisi")
            return
        }
        if (confirmPassword.isBlank()) {
            _state.value = _state.value.copy(error = "Konfirmasi password wajib diisi")
            return
        }
        if (newPassword != confirmPassword) {
            _state.value = _state.value.copy(error = "Password tidak cocok")
            return
        }

        viewModelScope.launch {
            forgotPasswordUseCase(email, dateOfBirth, placeOfBirth, newPassword, confirmPassword).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = result.data
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}