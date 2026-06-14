package com.example.model

import com.google.firebase.database.PropertyName

data class Transaction(
    val id: Long = 0,
    val amt: Double = 0.0,
    val desc: String = "",
    val type: String = "", // "udhaari" or "jama"
    val time: String = ""
)

data class Customer(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val balance: Double = 0.0,
    val history: List<Transaction> = emptyList()
)
