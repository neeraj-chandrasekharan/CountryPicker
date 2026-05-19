package com.njtech.countrypicker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: Map<String, String>,
    val dialCode: String,
    val areaCode: String? = null,
    val code: String,
    val flag: String
)
