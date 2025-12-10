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

class ReportMonthFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var barChart: BarChart
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_month, container, false)

        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        barChart = view.findViewById(R.id.barChart)
        tvIncome = view.findViewById(R.id.tvIncome)
        tvExpense = view.findViewById(R.id.tvExpense)
        tvBalance = view.findViewById(R.id.tvBalance)

        setupChartStyle()
        observeData()

        return view
    }

    private fun setupChartStyle() {
        barChart.description.isEnabled = false // Tắt chữ Description label nhỏ ở góc
        barChart.setDrawGridBackground(false)

        // Cấu hình trục X (Danh mục)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // Chỉ hiện số nguyên
        xAxis.labelRotationAngle = -45f // Xoay chữ cho đỡ chồng chéo nếu dài

        // Cấu hình trục Y (Tiền) - Bên trái
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false // Tắt trục phải cho đỡ rối

        barChart.animateY(1000) // Hiệu ứng hiện lên
    }

    private fun observeData() {
        val formatter = DecimalFormat("#,### đ")

        // 1. Quan sát dữ liệu biểu đồ
        viewModel.chartData.observe(viewLifecycleOwner) { entries ->
            val dataSet = BarDataSet(entries, "Chi theo danh mục")
            dataSet.color = Color.parseColor("#5C7872") // Màu xanh của App
            dataSet.valueTextSize = 12f

            val data = BarData(dataSet)
            data.barWidth = 0.5f // Độ rộng cột

            barChart.data = data
            barChart.invalidate() // Vẽ lại biểu đồ
        }

        // 2. Quan sát nhãn (Category names) để gán vào trục X
        viewModel.categoryLabels.observe(viewLifecycleOwner) { labels ->
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        }

        // 3. Quan sát số liệu tổng
        viewModel.totalIncome.observe(viewLifecycleOwner) {
            tvIncome.text = "Thu: ${formatter.format(it)}"
        }
        viewModel.totalExpense.observe(viewLifecycleOwner) {
            tvExpense.text = "Chi: ${formatter.format(it)}"
        }
        viewModel.balance.observe(viewLifecycleOwner) {
            tvBalance.text = "Số dư: ${formatter.format(it)}"
        }
    }
}