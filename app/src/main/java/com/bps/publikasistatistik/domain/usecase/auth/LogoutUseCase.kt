package com.bps.publikasistatistik.domain.usecase.auth

import com.bps.publikasistatistik.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Operator invoke agar bisa dipanggil seperti fungsi: logoutUseCase()
    suspend operator fun invoke() {
        repository.logout()
    }
}