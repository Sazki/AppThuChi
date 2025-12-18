package com.example.appcuoiky.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appcuoiky.R
import com.example.appcuoiky.model.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate



class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("AppSetting", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("Is_Dark_Mode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailLogin = findViewById<EditText>(R.id.email2)
        val passLogin = findViewById<EditText>(R.id.password2)

        findViewById<Button>(R.id.login).setOnClickListener {
            val email = emailLogin.text.toString().trim()
            val pass = passLogin.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser!!.uid
                        fetchUserAndOpenHome(uid, showGreeting = true)
                    } else {
                        Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        findViewById<Button>(R.id.singin1).setOnClickListener {
            openFragment(dangki())
        }

        findViewById<Button>(R.id.quen).setOnClickListener {
            openFragment(quenmatkhau())
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null && supportFragmentManager.backStackEntryCount == 0) {
            val uid = auth.currentUser!!.uid
            fetchUserAndOpenHome(uid, showGreeting = false)
        }
    }

    private fun fetchUserAndOpenHome(uid: String, showGreeting: Boolean) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = snapshot.toObject(users::class.java)

                    if (showGreeting) {
                        Toast.makeText(this, "Chào ${user?.name}", Toast.LENGTH_SHORT).show()
                    }

                    val fragment = home()
                    val bundle = Bundle()
                    bundle.putString("userId", user?.userId)
                    bundle.putString("name", user?.name)
                    fragment.arguments = bundle

                    openFragment(fragment)
                } else {
                    Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi lấy dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FL1, fragment)
            .addToBackStack(null)
            .commit()
    }
}