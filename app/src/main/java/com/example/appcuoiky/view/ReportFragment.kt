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

class ReportFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_container, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // Setup ViewPager với Adapter
        val adapter = ReportPagerAdapter(this)
        viewPager.adapter = adapter

        // Kết nối Tab với ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Báo cáo Tháng"
                1 -> tab.text = "Báo cáo Năm"
            }
        }.attach()

        return view
    }

    // Adapter để chuyển đổi giữa 2 màn hình con
    class ReportPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReportMonthFragment() // Màn hình chính bạn muốn
                else -> ReportYearFragment() // Màn hình năm (làm sau)
            }
        }
    }
}