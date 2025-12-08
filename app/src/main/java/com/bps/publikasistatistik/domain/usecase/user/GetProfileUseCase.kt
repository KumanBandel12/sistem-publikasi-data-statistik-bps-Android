package com.bps.publikasistatistik.domain.usecase.user

import com.bps.publikasistatistik.domain.repository.UserRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.getProfile()
}