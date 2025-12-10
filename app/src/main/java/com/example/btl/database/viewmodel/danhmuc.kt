package com.example.btl.database.viewmodel

data class danhmuc(
    val id: Int,
    val name: String,
    val iconRes: Int,
    var isSelected: Boolean = false
)