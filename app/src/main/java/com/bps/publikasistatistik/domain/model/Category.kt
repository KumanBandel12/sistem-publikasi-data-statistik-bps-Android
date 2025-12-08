package com.bps.publikasistatistik.domain.model

data class Category(
    val id: Long,
    val name: String,
    val description: String?,
    val totalPublications: Long
)