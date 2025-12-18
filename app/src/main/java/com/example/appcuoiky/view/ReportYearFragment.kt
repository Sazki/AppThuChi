package com.example.appcuoiky.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat
import java.util.Calendar

class ReportYearFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var barChart: BarChart
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvCurrentYear: TextView
    private lateinit var btnPrevYear: ImageView
    private lateinit var btnNextYear: ImageView
    private lateinit var tabLayout: TabLayout

    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentType = "CHI" // Mặc định xem Chi tiêu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_year, container, false)

        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        initViews(view)

        setupChartStyle()

        setupEvents()

        observeData()

        reloadChart()

        return view
    }

    private fun initViews(view: View) {
        barChart = view.findViewById(R.id.yearBarChart)
        tvIncome = view.findViewById(R.id.tvYearIncome)
        tvExpense = view.findViewById(R.id.tvYearExpense)
        tvBalance = view.findViewById(R.id.tvYearBalance)
        tvCurrentYear = view.findViewById(R.id.tvCurrentYear)
        btnPrevYear = view.findViewById(R.id.btnPrevYear)
        btnNextYear = view.findViewById(R.id.btnNextYear)
        tabLayout = view.findViewById(R.id.tabLayoutYear)
    }

    private fun setupEvents() {
        updateYearDisplay()

        btnPrevYear.setOnClickListener {
            currentYear--
            updateYearDisplay()
            reloadChart()
        }
        btnNextYear.setOnClickListener {
            currentYear++
            updateYearDisplay()
            reloadChart()
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> currentType = "CHI"
                    1 -> currentType = "THU"
                }
                reloadChart()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateYearDisplay() {
        tvCurrentYear.text = "Năm $currentYear"
    }

    private fun reloadChart() {
        viewModel.loadYearlyDataFromFirestore(currentYear, currentType)
    }

    private fun setupChartStyle() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 12 // Hiển thị đủ 12 tháng
        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = 11.5f

        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
    }

    private fun observeData() {
        val formatter = DecimalFormat("#,### đ")

        viewModel.yearlyChartData.observe(viewLifecycleOwner) { entries ->
            val label = if (currentType == "CHI") "Chi tiêu theo tháng" else "Thu nhập theo tháng"
            val colorCode = if (currentType == "CHI") "#FF5252" else "#4CAF50"

            val dataSet = BarDataSet(entries, label)
            dataSet.color = Color.parseColor(colorCode)
            dataSet.valueTextSize = 10f
            dataSet.valueTextColor = Color.BLACK

            val data = BarData(dataSet)
            data.barWidth = 0.6f

            barChart.data = data
            barChart.invalidate()
        }

        viewModel.yearlyLabels.observe(viewLifecycleOwner) { labels ->
            if (labels.size == 12) {
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            }
        }

        viewModel.yearTotalIncome.observe(viewLifecycleOwner) {
            tvIncome.text = formatter.format(it)
        }
        viewModel.yearTotalExpense.observe(viewLifecycleOwner) {
            tvExpense.text = formatter.format(it)
        }
        viewModel.yearBalance.observe(viewLifecycleOwner) {
            tvBalance.text = formatter.format(it)
        }
    }
}