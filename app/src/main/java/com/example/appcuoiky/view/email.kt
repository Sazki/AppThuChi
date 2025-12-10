package com.example.btl.database.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.btl.R


class email : Fragment() {
    private lateinit var Button: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email, container, false)
        val xacnhan = view.findViewById<Button>(R.id.xacnhan)

        val quaylai = view.findViewById<Button>(R.id.quaylai1)

        xacnhan.setOnClickListener {
            val fragmentA = Navigation()

            parentFragmentManager.beginTransaction()
                .replace(R.id.FL3, fragmentA)
                .addToBackStack(null)
                .commit()
        }
        quaylai.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }


}