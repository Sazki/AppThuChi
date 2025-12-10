package com.example.appcuoiky.model

data class Transaction(
    var id: String = "",           // ID của document (Firebase tự sinh)
    var userId: String = "",       // ID người dùng (để biết ai nhập)
    var content: String = "",      // Ví dụ: Ăn sáng, Tiền lương
    var amount: Double = 0.0,      // Số tiền
    var type: String = "",         // "THU" hoặc "CHI"
    var date: String = "",         // Ngày: dd/MM/yyyy
    var note: String = ""          // Ghi chú thêm
)