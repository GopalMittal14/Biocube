package com.biocube.core.domain.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: Int? = null,
    val type: ServiceType,
    val isAvailable: Boolean = false
)

enum class ServiceType {
    E_VISA,
    ATTENDANCE,
    CHECK_IN,
    CHECK_OUT,
    INSURANCE,
    BANKING,
    OTHER
}
