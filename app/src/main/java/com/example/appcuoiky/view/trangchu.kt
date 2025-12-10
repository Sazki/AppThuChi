package com.example.btl.database.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.btl.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class trangchu : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_trangchu, container, false)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.chi -> {
                    replaceFragment(chi())
                    true
                }
                R.id.thu -> {
                    replaceFragment(thu())
                    true
                }
                else -> false
            }
        }

        // Load mặc định
        if (savedInstanceState == null) {
            replaceFragment(chi())
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}