package com.example.btl.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.btl.database.MainActivity
import com.example.btl.database.ui.chi
import com.example.btl.database.ui.trangchu
import com.example.btl.database.ui.thu
import com.example.btl.database.ui.dangki
import com.example.btl.database.ui.quenmatkhau

class adapter {


    class Adapter(activity: MainActivity) : FragmentStateAdapter(activity){
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> trangchu()
                1 -> chi()
                2 -> quenmatkhau()
                3 -> dangki()
                else -> trangchu()
            } as Fragment
        }

        override fun getItemCount(): Int = 4

    }

}