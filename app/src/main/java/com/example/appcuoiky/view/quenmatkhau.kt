package com.example.btl.database.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.btl.R
import com.example.btl.database.MainActivity


class quenmatkhau : Fragment() {

    private lateinit var Button : Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.quenmatkhau, container, false)
        val thaydoi = view.findViewById<Button>(R.id.thaydoi)
        val quaylai = view.findViewById<Button>(R.id.quaylai)


        thaydoi.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        quaylai.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}