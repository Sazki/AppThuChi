package com.example.appcuoiky.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appcuoiky.database.MainActivity
import com.example.appcuoiky.database.ui.chi
import com.example.appcuoiky.database.ui.trangchu
import com.example.appcuoiky.database.ui.thu
import com.example.appcuoiky.database.ui.dangki
import com.example.appcuoiky.database.ui.quenmatkhau

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