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
import com.example.appcuoiky.model.users
import com.google.firebase.auth.FirebaseAuth
import com.example.appcuoiky.viewmodel.Emailsender
import com.google.firebase.firestore.FirebaseFirestore

class email : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val so1 = view.findViewById<EditText>(R.id.so1)
        val so2 = view.findViewById<EditText>(R.id.so2)
        val so3 = view.findViewById<EditText>(R.id.so3)
        val so4 = view.findViewById<EditText>(R.id.so4)
        val xacnhan = view.findViewById<Button>(R.id.xacnhan)
        val guilai = view.findViewById<Button>(R.id.guilai)
        val quaylai = view.findViewById<Button>(R.id.quaylai1)

        val shared = requireActivity().getSharedPreferences("otp_data", 0)
        val savedOtp = shared.getString("otp", "")
        val email = shared.getString("email", "")
        val password = shared.getString("password", "")
        val name = shared.getString("name", "")

        xacnhan.setOnClickListener {
            val userOtp = so1.text.toString() + so2.text.toString() +
                    so3.text.toString() + so4.text.toString()

            val latestOtp = shared.getString("otp", "")

            if (userOtp == latestOtp) {
                Toast.makeText(requireContext(), "Xác thực thành công!", Toast.LENGTH_SHORT).show()

                auth.createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser!!.uid

                            val userObject = users(
                                userId = uid,
                                avatar = "",
                                email = email,
                                matkhau = password,
                                name = name!!
                            )

                            firestore.collection("users")
                                .document(uid)
                                .set(userObject)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

//                                    parentFragmentManager.beginTransaction()
//                                        .replace(R.id.FL3, MainActivity())
//                                        .commit()
                                    parentFragmentManager.popBackStack()
                                }
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Sai mã OTP!", Toast.LENGTH_SHORT).show()
            }
        }


        guilai.setOnClickListener {
            val newOtp = (1000..9999).random().toString()

            val editor = shared.edit()
            editor.putString("otp", newOtp)
            editor.apply()

            Thread {
                try {
                    val sender = com.example.appcuoiky.viewmodel.Emailsender(
                        "tranlinh22881111@gmail.com",
                        "ekuu eldy cryc tvgy"
                    )
                    sender.sendEmail(
                        email!!,
                        "Mã OTP mới của bạn",
                        "Mã OTP mới là: $newOtp"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()

            Toast.makeText(requireContext(), "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show()
        }


        quaylai.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
