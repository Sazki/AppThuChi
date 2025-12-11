package com.example.appcuoiky.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.adapter.CalendarAdapter
import com.example.appcuoiky.viewmodel.MainViewModel
import java.text.DecimalFormat

class CalendarFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var tvDate: TextView
    private lateinit var tvThu: TextView
    private lateinit var tvChi: TextView
    private lateinit var tvSoDu: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews(view)
        setupCalendar()
        observeData()
        handleEvents()

        return view
    }

    private fun initViews(view: View) {
        tvDate = view.findViewById(R.id.tvDate)
        tvThu = view.findViewById(R.id.tvTotalIncome)
        tvChi = view.findViewById(R.id.tvTotalExpense)
        tvSoDu = view.findViewById(R.id.tvBalance)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        rvCalendar = view.findViewById(R.id.rvCalendar)
    }

    private fun setupCalendar() {
        val layoutManager = GridLayoutManager(requireContext(), 7)
        rvCalendar.layoutManager = layoutManager

        // Khởi tạo Adapter với callback xử lý Click
        calendarAdapter = CalendarAdapter(emptyList()) { selectedDay ->
            // Khi click vào ngày -> Gọi ViewModel tính toán lại số liệu ngày đó
            mainViewModel.selectDate(selectedDay)
        }
        rvCalendar.adapter = calendarAdapter
    }

    private fun observeData() {
        val formatter = DecimalFormat("#,### đ")

        mainViewModel.currentMonth.observe(viewLifecycleOwner) { month ->
            tvDate.text = month
        }

        mainViewModel.dayList.observe(viewLifecycleOwner) { days ->
            calendarAdapter.updateData(days)
        }

        mainViewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            tvThu.text = "Thu: ${formatter.format(income)}"
        }

        mainViewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            tvChi.text = "Chi: ${formatter.format(expense)}"
        }

        mainViewModel.balance.observe(viewLifecycleOwner) { balance ->
            tvSoDu.text = "Số dư: ${formatter.format(balance)}"
        }
    }

    private fun handleEvents() {
        btnNext.setOnClickListener { mainViewModel.nextMonth() }
        btnPrev.setOnClickListener { mainViewModel.previousMonth() }
    }
}