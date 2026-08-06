package com.cephcoding.core.domain.model

enum class Currency(val code: String, val symbol: String, val displayName: String) {
    KES("KES", "KSh", "Kenyan Shilling"),
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    GBP("GBP", "£", "British Pound"),
    JPY("JPY", "¥", "Japanese Yen"),
    CAD("CAD", "CA$", "Canadian Dollar"),
    AUD("AUD", "A$", "Australian Dollar"),
    INR("INR", "₹", "Indian Rupee"),
    ZAR("ZAR", "R", "South African Rand"),
    AED("AED", "AED", "UAE Dirham")
}
