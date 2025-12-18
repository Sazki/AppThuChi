package com.example.appcuoiky.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appcuoiky.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class quenmatkhau : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.quenmatkhau, container, false)

        val thaydoi = view.findViewById<Button>(R.id.thaydoi)
        val quaylai = view.findViewById<Button>(R.id.quaylai)
        val email = view.findViewById<EditText>(R.id.email2)

        val auth = FirebaseAuth.getInstance()

        thaydoi.setOnClickListener {
            val emailText = email.text.toString().trim()

            if (emailText.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Đã gửi email đặt lại mật khẩu",
                            Toast.LENGTH_LONG
                        ).show()

                        parentFragmentManager.popBackStack()
                    } else {
                        val error = task.exception
                        if (error is FirebaseAuthInvalidUserException) {
                            Toast.makeText(
                                requireContext(),
                                "Email không tồn tại trong hệ thống",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                error?.message ?: "Có lỗi xảy ra",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        quaylai.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
