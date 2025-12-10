package com.example.btl.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.btl.database.MainActivity
import com.example.btl.database.ui.chi
import com.example.btl.database.ui.thu
import com.example.btl.database.ui.dangki
import com.example.btl.database.ui.quenmatkhau
import com.example.btl.database.ui.thu_chi

class adapter {


    class Adapter(activity: MainActivity) : FragmentStateAdapter(activity){
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> thu_chi()
                1 -> chi()
                2 -> quenmatkhau()
                3 -> dangki()
                else -> thu_chi()
            } as Fragment
        }

        override fun getItemCount(): Int = 4

    }

}