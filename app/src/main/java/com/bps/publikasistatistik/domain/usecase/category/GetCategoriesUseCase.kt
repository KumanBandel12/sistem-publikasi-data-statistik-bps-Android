package com.bps.publikasistatistik.domain.usecase.category

import com.bps.publikasistatistik.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke() = repository.getCategories()
}