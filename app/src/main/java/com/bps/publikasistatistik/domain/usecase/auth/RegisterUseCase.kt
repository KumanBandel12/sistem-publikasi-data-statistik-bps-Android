package com.bps.publikasistatistik.domain.usecase.auth

import com.bps.publikasistatistik.domain.repository.AuthRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, pass: String): Flow<Resource<String>> {
        return repository.register(username, email, pass)
    }
}