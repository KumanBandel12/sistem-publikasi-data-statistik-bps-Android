package com.bps.publikasistatistik.domain.usecase.auth

import com.bps.publikasistatistik.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) = repository.forgotPassword(email)
}