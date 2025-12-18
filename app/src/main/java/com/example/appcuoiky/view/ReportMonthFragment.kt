package com.example.appcuoiky.view

import android.graphics.Canvas
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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class ReportMonthFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var barChart: BarChart
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tabLayout: TabLayout

    private lateinit var tvCurrentMonth: TextView
    private lateinit var btnPrevMonth: ImageView
    private lateinit var btnNextMonth: ImageView

    private var currentType = "CHI"
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_month, container, false)
        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        barChart = view.findViewById(R.id.barChart)
        tvIncome = view.findViewById(R.id.tvIncome)
        tvExpense = view.findViewById(R.id.tvExpense)
        tvBalance = view.findViewById(R.id.tvBalance)
        tabLayout = view.findViewById(R.id.tabLayout)
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth)
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth)
        btnNextMonth = view.findViewById(R.id.btnNextMonth)

        setupChartStyle()
        setupTabLayout()
        setupMonthNavigation()
        observeData()

        return view
    }

    private fun formatMultiLineLabel(label: String): String {
        val limit = 10 // Giới hạn số ký tự để bắt đầu xem xét xuống dòng
        if (label.length <= limit) return label

        val splitIndex = label.indexOf(' ', 7)
        return if (splitIndex != -1) {
            label.substring(0, splitIndex) + "\n" + label.substring(splitIndex + 1)
        } else {
            if (label.length > 15) label.substring(0, 13) + "..." else label
        }
    }

    class MultiLineXAxisRenderer(
        viewPortHandler: ViewPortHandler?,
        xAxis: XAxis?,
        trans: Transformer?
    ) : XAxisRenderer(viewPortHandler, xAxis, trans) {
        override fun drawLabel(
            c: Canvas?,
            formattedLabel: String?,
            x: Float,
            y: Float,
            anchor: MPPointF?,
            angleDegrees: Float
        ) {
            val lines = formattedLabel?.split("\n") ?: return
            for (i in lines.indices) {
                val vOffset = i * mAxisLabelPaint.textSize
                Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees)
            }
        }
    }

    private fun setupChartStyle() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)

        barChart.extraBottomOffset = 30f

        barChart.setXAxisRenderer(
            MultiLineXAxisRenderer(
                barChart.viewPortHandler,
                barChart.xAxis,
                barChart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 0f // Để chữ thẳng đứng như bạn muốn

        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
    }

    private fun observeData() {
        val formatter = DecimalFormat("#,### đ")

        viewModel.chartData.observe(viewLifecycleOwner) { entries ->
            val label = if (currentType == "CHI") "Chi tiêu" else "Thu nhập"
            val colorCode = if (currentType == "CHI") "#FF5252" else "#4CAF50"

            val dataSet = BarDataSet(entries, label)
            dataSet.color = Color.parseColor(colorCode)
            dataSet.valueTextSize = 12f

            val data = BarData(dataSet)
            data.barWidth = 0.5f
            barChart.data = data
            barChart.invalidate()
        }

        viewModel.categoryLabels.observe(viewLifecycleOwner) { labels ->
            val multiLineLabels = labels.map { formatMultiLineLabel(it) }
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(multiLineLabels)
        }

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

    private fun setupMonthNavigation() {
        updateMonthDisplay()
        btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            reloadChart()
        }
        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            reloadChart()
        }
    }

    private fun updateMonthDisplay() {
        val fmt = SimpleDateFormat("MM/yyyy", Locale("vi", "VN"))
        tvCurrentMonth.text = "Tháng " + fmt.format(calendar.time)
    }

    private fun setupTabLayout() {
        if (tabLayout.tabCount > 0) tabLayout.getTabAt(0)?.select()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { currentType = "CHI"; reloadChart() }
                    1 -> { currentType = "THU"; reloadChart() }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun reloadChart() {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        viewModel.loadDataFromFirestore(month, year, currentType)
    }

    override fun onResume() {
        super.onResume()
        reloadChart()
    }
}