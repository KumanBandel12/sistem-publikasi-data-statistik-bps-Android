package com.bps.publikasistatistik.domain.model

data class Category(
    val id: Long,
    val name: String,
    val description: String,
    val totalPublications: Long,
    val subCategories: List<Category> = emptyList()
){
    override fun toString(): String {
        return "Category(id=$id, name='$name')"
    }
}