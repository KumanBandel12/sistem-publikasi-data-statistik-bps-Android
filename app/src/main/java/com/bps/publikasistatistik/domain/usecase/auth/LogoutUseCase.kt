package com.bps.publikasistatistik.domain.usecase.auth

import com.bps.publikasistatistik.domain.repository.AuthRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Resource<Boolean>> {
        return repository.logout()
    }
}