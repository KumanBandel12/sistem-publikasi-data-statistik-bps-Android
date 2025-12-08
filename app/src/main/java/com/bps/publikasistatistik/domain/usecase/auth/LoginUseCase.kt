package com.bps.publikasistatistik.domain.usecase.auth

import com.bps.publikasistatistik.domain.repository.AuthRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Flow<Resource<String>> {
        if (email.isBlank() || pass.isBlank()) {
            // Contoh validasi bisnis sederhana
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("Email dan password tidak boleh kosong"))
            }
        }
        return repository.login(email, pass)
    }
}