package com.example.appcuoiky.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appcuoiky.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class home : Fragment() {


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_navigation, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        replaceChildFragment(thu_chi())
                        true
                    }
                    R.id.thongke -> {
                        replaceChildFragment(CalendarFragment())
                        true
                    }
                    R.id.ai -> {
                        replaceChildFragment(ReportFragment())
                        true
                    }
                    R.id.taikhoan -> {
                        replaceChildFragment(SettingsFragment())
                        true
                    }
                    else -> false
                }
            }

            replaceChildFragment(thu_chi())

        }

        private fun replaceChildFragment(fragment: Fragment) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
