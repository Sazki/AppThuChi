package com.example.appcuoiky.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appcuoiky.R
import com.example.appcuoiky.viewmodel.ReportViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat

class ReportYearFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var barChart: BarChart
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvYearDisplay: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_year, container, false)

        // Kết nối ViewModel
        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        // Ánh xạ View
        barChart = view.findViewById(R.id.yearBarChart)
        tvIncome = view.findViewById(R.id.tvYearIncome)
        tvExpense = view.findViewById(R.id.tvYearExpense)
        tvBalance = view.findViewById(R.id.tvYearBalance)
        tvYearDisplay = view.findViewById(R.id.tvYearDisplay)

        setupChartStyle()
        observeData()

        return view
    }

    private fun setupChartStyle() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)

        // Cấu hình trục X (Hiển thị tháng)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 12 // Hiện đủ 12 tháng

        // Cấu hình trục Y
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false

        barChart.animateY(1000)
    }

    private fun observeData() {
        val formatter = DecimalFormat("#,### đ")

        // 1. Quan sát dữ liệu biểu đồ NĂM
        viewModel.yearlyChartData.observe(viewLifecycleOwner) { entries ->
            val dataSet = BarDataSet(entries, "Chi tiêu theo tháng")
            dataSet.color = Color.parseColor("#5C7872") // Màu xanh chủ đạo
            dataSet.valueTextSize = 10f

            val data = BarData(dataSet)
            data.barWidth = 0.6f // Cột to hơn chút

            barChart.data = data
            barChart.invalidate()
        }

        // 2. Gán nhãn trục X (T1 - T12)
        viewModel.yearlyLabels.observe(viewLifecycleOwner) { labels ->
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        }

        // 3. Quan sát tiền
        viewModel.yearTotalIncome.observe(viewLifecycleOwner) {
            tvIncome.text = formatter.format(it)
        }
        viewModel.yearTotalExpense.observe(viewLifecycleOwner) {
            tvExpense.text = formatter.format(it)
        }
        viewModel.yearBalance.observe(viewLifecycleOwner) {
            tvBalance.text = "Số dư: ${formatter.format(it)}"
        }
    }
}