package com.example.appcuoiky.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class SettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _username = MutableLiveData<String>()
    private val _userEmail = MutableLiveData<String>()
    private val _avatarData = MutableLiveData<String>() // Lưu chuỗi Base64 của ảnh
    private val _isLoggedIn = MutableLiveData<Boolean>()

    val username: LiveData<String> get() = _username
    val userEmail: LiveData<String> get() = _userEmail
    val avatarData: LiveData<String> get() = _avatarData
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
            _avatarData.value = ""
            return
        }

        _isLoggedIn.value = true
        _userEmail.value = user.email ?: ""

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _username.value = doc.getString("name") ?: "Người dùng"
                    _avatarData.value = doc.getString("avatar") ?: ""
                }
            }
            .addOnFailureListener {
                _username.value = "Người dùng"
            }
    }
    fun saveAvatarToFirestore(bitmap: Bitmap, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        firestore.collection("users")
            .document(user.uid)
            .update("avatar", base64String)
            .addOnSuccessListener {
                _avatarData.value = base64String
                onSuccess()
            }
    }

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
        _avatarData.value = ""
    }
}