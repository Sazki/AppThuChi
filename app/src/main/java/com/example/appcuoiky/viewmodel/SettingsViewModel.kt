package com.example.appcuoiky.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    private val _userEmail = MutableLiveData<String>()
    private val _isLoggedIn = MutableLiveData<Boolean>()

    val username: LiveData<String> get() = _username
    val userEmail: LiveData<String> get() = _userEmail
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        // Giả lập dữ liệu người dùng đã đăng nhập
        _username.value = "Nguyễn Văn A"
        _userEmail.value = "nguyenvana@gmail.com"
        _isLoggedIn.value = true
    }
    fun updateUserInfo(newName: String) {
        _username.value = newName
        // Sau này sẽ thêm code update lên Firebase ở đây
    }


    fun logout() {
        // Xử lý logic đăng xuất (xóa token, clear data...)
        _isLoggedIn.value = false
        _username.value = "Khách"
        _userEmail.value = "Chưa đăng nhập"
    }
}