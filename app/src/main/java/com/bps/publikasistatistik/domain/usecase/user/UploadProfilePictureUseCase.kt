package com.bps.publikasistatistik.domain.usecase.user

import com.bps.publikasistatistik.domain.repository.UserRepository
import java.io.File
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(file: File) = repository.uploadProfilePicture(file)
}