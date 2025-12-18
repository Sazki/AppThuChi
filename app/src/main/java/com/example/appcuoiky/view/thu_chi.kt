package com.example.appcuoiky.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.appcuoiky.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class thu_chi : Fragment() {

        private lateinit var tabLayout: TabLayout
        private lateinit var viewPager: ViewPager2

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.thu_chi, container, false)

            tabLayout = view.findViewById(R.id.tabLayout)
            viewPager = view.findViewById(R.id.viewPager)

            val adapter = ReportPagerAdapter(this)
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Chi tiêu"
                    1 -> tab.text = "Thu nhập"
                }
            }.attach()

            return view
        }
        class ReportPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> chi()
                    else -> thu()
                }
            }
        }
    }
