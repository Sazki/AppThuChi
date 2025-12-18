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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.adapter.CalendarAdapter
import com.example.appcuoiky.adapter.TransactionAdapter
import com.example.appcuoiky.viewmodel.MainViewModel
import java.text.DecimalFormat

class CalendarFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var tvDate: TextView
    private lateinit var tvThu: TextView
    private lateinit var tvChi: TextView
    private lateinit var tvSoDu: TextView
    private lateinit var tvTransactionTitle: TextView
    private lateinit var tvEmptyTransaction: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews(view)
        setupCalendar()
        setupTransactionList()
        observeData()
        handleEvents()

        return view
    }

    private fun initViews(view: View) {
        tvDate = view.findViewById(R.id.tvDate)
        tvThu = view.findViewById(R.id.tvTotalIncome)
        tvChi = view.findViewById(R.id.tvTotalExpense)
        tvSoDu = view.findViewById(R.id.tvBalance)
        tvTransactionTitle = view.findViewById(R.id.tvTransactionTitle)
        tvEmptyTransaction = view.findViewById(R.id.tvEmptyTransaction)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        rvCalendar = view.findViewById(R.id.rvCalendar)
        rvTransactions = view.findViewById(R.id.rvTransactions)
    }

    private fun setupCalendar() {
        val layoutManager = GridLayoutManager(requireContext(), 7)
        rvCalendar.layoutManager = layoutManager

        calendarAdapter = CalendarAdapter(emptyList()) { selectedDay ->
            mainViewModel.selectDate(selectedDay)
        }
        rvCalendar.adapter = calendarAdapter
    }

    private fun setupTransactionList() {
        val layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.layoutManager = layoutManager

        transactionAdapter = TransactionAdapter(emptyList())
        rvTransactions.adapter = transactionAdapter
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

        mainViewModel.selectedDateTransactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isEmpty()) {
                rvTransactions.visibility = View.GONE
                tvEmptyTransaction.visibility = View.VISIBLE
                tvEmptyTransaction.text = "Không có giao dịch trong ngày này"
            } else {
                rvTransactions.visibility = View.VISIBLE
                tvEmptyTransaction.visibility = View.GONE
                transactionAdapter.updateData(transactions)
            }
        }

        mainViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date.isNotEmpty()) {
                tvTransactionTitle.text = "Lịch sử giao dịch - $date"
            } else {
                tvTransactionTitle.text = "Lịch sử giao dịch"
            }
        }
    }

    private fun handleEvents() {
        btnNext.setOnClickListener { mainViewModel.nextMonth() }
        btnPrev.setOnClickListener { mainViewModel.previousMonth() }
    }
}