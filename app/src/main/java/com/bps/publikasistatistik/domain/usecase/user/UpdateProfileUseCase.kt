package com.bps.publikasistatistik.domain.usecase.user

import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User) = repository.updateProfile(user)
}