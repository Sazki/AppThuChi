package com.example.btl.database.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.btl.R
import com.example.btl.database.ui.chi
import com.example.btl.database.ui.dangki
import com.example.btl.database.ui.trangchu
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navigation : Fragment() {


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
                    replaceChildFragment(trangchu())
                    true
                }
                R.id.thongke -> {
                    replaceChildFragment(chi())
                    true
                }
                R.id.ai -> {
                    replaceChildFragment(quenmatkhau())
                    true
                }
                R.id.taikhoan -> {
                    replaceChildFragment(dangki())
                    true
                }
                else -> false
            }
        }

        // Mặc định mở trang chủ
        replaceChildFragment(trangchu())
    }

    private fun replaceChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}