package com.example.appcuoiky.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appcuoiky.view.CalendarFragment
import com.example.appcuoiky.view.MainActivity
import com.example.appcuoiky.view.SettingsFragment
import com.example.appcuoiky.view.thu_chi

class adapter {


    class Adapter(activity: MainActivity) : FragmentStateAdapter(activity){
        @SuppressLint("RestrictedApi")
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> thu_chi()
                1 -> CalendarFragment()
                2 -> ReportFragment()
                3 -> SettingsFragment()
                else -> thu_chi()
            } as Fragment
        }

        override fun getItemCount(): Int = 4

    }

}