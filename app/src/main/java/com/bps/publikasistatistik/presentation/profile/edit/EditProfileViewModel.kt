package com.bps.publikasistatistik.presentation.profile.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.domain.usecase.user.GetProfileUseCase
import com.bps.publikasistatistik.domain.usecase.user.UpdateProfileUseCase
import com.bps.publikasistatistik.domain.usecase.user.UploadProfilePictureUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : ViewModel() {

    private val _state = mutableStateOf(EditProfileUiState())
    val state: State<EditProfileUiState> = _state

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                if (result is Resource.Success && result.data != null) {
                    val user = result.data
                    _state.value = _state.value.copy(
                        profilePictureUrl = user.profilePictureUrl,
                        fullName = user.fullName ?: "",
                        username = user.username,
                        email = user.email,
                        phoneNumber = user.phoneNumber ?: "",
                        address = user.address ?: "",
                        gender = user.gender ?: "Laki-laki",
                        placeOfBirth = user.placeOfBirth ?: "",
                        dateOfBirth = user.dateOfBirth ?: ""
                    )
                }
            }
        }
    }

    // Fungsi untuk update field saat user mengetik
    fun onEvent(event: EditProfileEvent) {
        when(event) {
            is EditProfileEvent.EnteredFullName -> _state.value = _state.value.copy(fullName = event.value)
            is EditProfileEvent.EnteredUsername -> _state.value = _state.value.copy(username = event.value)
            is EditProfileEvent.EnteredEmail -> _state.value = _state.value.copy(email = event.value)
            is EditProfileEvent.EnteredPhone -> _state.value = _state.value.copy(phoneNumber = event.value)
            is EditProfileEvent.EnteredAddress -> _state.value = _state.value.copy(address = event.value)
            is EditProfileEvent.SelectedGender -> _state.value = _state.value.copy(gender = event.value)
            is EditProfileEvent.EnteredPlaceOfBirth -> _state.value = _state.value.copy(placeOfBirth = event.value)
            is EditProfileEvent.EnteredDateOfBirth -> _state.value = _state.value.copy(dateOfBirth = event.value)
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value

            // Buat objek User baru (ID bisa dummy karena di backend ambil dari Token)
            val updatedUser = User(
                id = 0,
                username = currentState.username,
                email = currentState.email,
                role = "", // Tidak diubah
                fullName = currentState.fullName,
                gender = currentState.gender,
                placeOfBirth = currentState.placeOfBirth,
                dateOfBirth = currentState.dateOfBirth.ifEmpty { null },
                phoneNumber = currentState.phoneNumber,
                address = currentState.address,
                profilePictureUrl = null
            )

            updateProfileUseCase(updatedUser).collect { result ->
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

    fun uploadPicture(file: java.io.File) {
        viewModelScope.launch {
            uploadProfilePictureUseCase(file).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                        // Refresh data profil agar foto baru muncul
                        loadCurrentProfile()
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}

// Helper Sealed Class untuk event input (agar kode lebih rapi)
sealed class EditProfileEvent {
    data class EnteredFullName(val value: String) : EditProfileEvent()
    data class EnteredUsername(val value: String) : EditProfileEvent()
    data class EnteredEmail(val value: String) : EditProfileEvent()
    data class EnteredPhone(val value: String) : EditProfileEvent()
    data class EnteredAddress(val value: String) : EditProfileEvent()
    data class SelectedGender(val value: String) : EditProfileEvent()
    data class EnteredPlaceOfBirth(val value: String) : EditProfileEvent()
    data class EnteredDateOfBirth(val value: String) : EditProfileEvent()
}