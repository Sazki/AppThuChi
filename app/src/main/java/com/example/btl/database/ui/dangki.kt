package com.example.btl.database.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.btl.R
import com.example.btl.database.MainActivity


class dangki : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dangki, container, false)
        val next = view.findViewById<Button>(R.id.next)

        val dangnhap = view.findViewById<Button>(R.id.singin)

        next.setOnClickListener {
            val fragmentA = email()

            parentFragmentManager.beginTransaction()
                .replace(R.id.FL4, fragmentA)
                .addToBackStack(null)
                .commit()
        }
        dangnhap.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}