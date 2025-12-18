package com.example.appcuoiky.model

data class Transaction(
    var id: String = "",
    var userId: String = "",
    var content: String = "",
    var amount: Double = 0.0,
    var type: String = "",
    var date: String = "",
    var note: String = ""
)
data class users(
    var userId: String = "",
    var avatar: String = "",
    var email: String = "",
    var matkhau: String = "",
    var name: String = "",
)
data class category(
    var userId: String = "",
    var content: String = "",
    var icon: String = "",
    var name: String = "",
    var type: String = "",
)
