package com.example.appcuoiky.model

data class Transaction(
    var id: String = "",
    var content: String = "",
    var amount: Double = 0.0,
    var type: String = "", // Lưu quy ước: "THU" hoặc "CHI"
    var date: String = ""
)