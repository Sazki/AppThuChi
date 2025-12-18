package com.example.appcuoiky.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.appcuoiky.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ReportFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2
    private lateinit var fabAIAssistant: FloatingActionButton

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_container, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        fabAIAssistant = view.findViewById(R.id.fabAIAssistant)

        val adapter = ReportPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Báo cáo Tháng"
                1 -> tab.text = "Báo cáo Năm"
            }
        }.attach()

        setupAIAssistant()

        return view
    }

    private fun setupAIAssistant() {
        fabAIAssistant.setOnClickListener {
            openAIAssistant()
        }
    }

    private fun openAIAssistant() {
        if (currentUserId == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "Đang tổng hợp dữ liệu...", Toast.LENGTH_SHORT).show()

        calculateFinancialData { income, expense ->
            try {

                val intent = Intent(requireContext(), AIAssistantActivity::class.java).apply {
                    putExtra("monthly_income", income)
                    putExtra("monthly_expense", expense)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Không thể mở trợ lý AI: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateFinancialData(callback: (Double, Double) -> Unit) {
        if (currentUserId == null) {
            callback(0.0, 0.0)
            return
        }

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val monthYearString = String.format("/%02d/%d", month, year)

        firestore.collection("transactions")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                var totalIncome = 0.0
                var totalExpense = 0.0

                for (document in documents) {
                    val dateStr = document.getString("date") ?: ""

                    if (dateStr.endsWith(monthYearString)) {
                        val type = document.getString("type") ?: ""
                        val amount = document.getDouble("amount") ?: 0.0

                        when (type) {
                            "THU" -> totalIncome += amount
                            "CHI" -> totalExpense += amount
                        }
                    }
                }

                callback(totalIncome, totalExpense)
            }
            .addOnFailureListener {
                callback(0.0, 0.0)
                Toast.makeText(context, "Lỗi kết nối dữ liệu", Toast.LENGTH_SHORT).show()
            }
    }

    class ReportPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReportMonthFragment()
                else -> ReportYearFragment()
            }
        }
    }
}