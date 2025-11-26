package com.example.mimapa.data.model

data class CreditItemCcBySa3(
    val title: String,
    val author: String,
    val authorUrl: String,
    val sourceUrl: String,
    val licenseName: String = "CC BY-SA 3.0",
    val licenseUrl: String = "https://creativecommons.org/licenses/by-sa/3.0/"
)
