package com.bps.publikasistatistik.domain.usecase.user

import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.domain.repository.UserRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteProfilePictureUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<User>> = repository.deleteProfilePicture()
}