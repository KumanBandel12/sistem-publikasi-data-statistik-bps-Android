package com.bps.publikasistatistik.presentation.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.auth.LoginUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = mutableStateOf(LoginUiState())
    val state: State<LoginUiState> = _state

    private val _email = mutableStateOf("admin@bps.go.id") // Default biar cepat test
    val email: State<String> = _email

    private val _password = mutableStateOf("admin123") // Default biar cepat test
    val password: State<String> = _password

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            loginUseCase(email.value, password.value).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = LoginUiState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = LoginUiState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _state.value = LoginUiState(error = result.message)
                    }
                }
            }
        }
    }
}