package com.example.appcuoiky.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _username = MutableLiveData<String>()
    private val _userEmail = MutableLiveData<String>()
    private val _isLoggedIn = MutableLiveData<Boolean>()

    val username: LiveData<String> get() = _username
    val userEmail: LiveData<String> get() = _userEmail
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        loadUser()
    }

    fun loadUser() {
        val user = auth.currentUser

        if (user == null) {
            _isLoggedIn.value = false
            _username.value = "Khách"
            _userEmail.value = "Chưa đăng nhập"
            return
        }

        _isLoggedIn.value = true
        _userEmail.value = user.email ?: ""

        // Lấy name từ Firestore
        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _username.value = doc.getString("name") ?: "Người dùng"
                }
            }
            .addOnFailureListener {
                _username.value = "Người dùng"
            }
    }

    // 🔹 Update tên (Firestore)
    fun updateUserInfo(newName: String) {
        val user = auth.currentUser ?: return

        firestore.collection("users")
            .document(user.uid)
            .update("name", newName)
            .addOnSuccessListener {
                _username.value = newName
            }
    }

    fun logout() {
        auth.signOut()

        _isLoggedIn.value = false
        _username.value = "Khách"
        _userEmail.value = "Chưa đăng nhập"
    }
}
