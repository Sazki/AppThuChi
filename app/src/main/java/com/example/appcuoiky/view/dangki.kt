package com.example.appcuoiky.view

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appcuoiky.R
import com.example.appcuoiky.model.users
import com.example.appcuoiky.viewmodel.Emailsender

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class dangki : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val senderEmail = "tranlinh22881111@gmail.com"
    private val senderPassword = "ekuu eldy cryc tvgy"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dangki, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameInput = view.findViewById<EditText>(R.id.name1)
        val emailInput = view.findViewById<EditText>(R.id.email1)
        val passInput = view.findViewById<EditText>(R.id.password1)
        val next = view.findViewById<Button>(R.id.next)
        val singin = view.findViewById<Button>(R.id.singin)

        next.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !(email.endsWith("@gmail.com")||email.endsWith("@vku.udn.vn"))) {
                Toast.makeText(requireContext(), "Email phải đúng định dạng @gmail.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otp = Random.nextInt(1000, 9999).toString()

            val shared = requireActivity().getSharedPreferences("otp_data", 0)
            shared.edit().apply {
                putString("otp", otp)
                putString("email", email)
                putString("name", name)
                putString("password", password)
            }.apply()

            Thread {
                try {
                    val sender = Emailsender(senderEmail, senderPassword)
                    sender.sendEmail(
                        email,
                        "Mã xác thực tài khoản",
                        "Mã OTP của bạn là: $otp"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()

            Toast.makeText(requireContext(), "Đã gửi mã OTP vào email!", Toast.LENGTH_SHORT).show()

            parentFragmentManager.beginTransaction()
                .replace(R.id.FL4, email())
                .addToBackStack(null)
                .commit()
        }

        singin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
