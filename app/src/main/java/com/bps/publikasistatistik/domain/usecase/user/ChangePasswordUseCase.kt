package com.bps.publikasistatistik.domain.usecase.user

import com.bps.publikasistatistik.domain.repository.UserRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(old: String, new: String) = repository.changePassword(old, new)
}