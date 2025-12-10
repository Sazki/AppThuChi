package com.example.appcuoiky.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry

class ReportViewModel : ViewModel() {

    // Danh sách giá trị tiền (Y)
    private val _chartData = MutableLiveData<List<BarEntry>>()
    // Danh sách tên danh mục (X) tương ứng
    private val _categoryLabels = MutableLiveData<List<String>>()

    // Tổng thu chi
    private val _totalIncome = MutableLiveData<Double>()
    private val _totalExpense = MutableLiveData<Double>()
    private val _balance = MutableLiveData<Double>()

    val chartData: LiveData<List<BarEntry>> get() = _chartData
    val categoryLabels: LiveData<List<String>> get() = _categoryLabels
    val totalIncome: LiveData<Double> get() = _totalIncome
    val totalExpense: LiveData<Double> get() = _totalExpense
    val balance: LiveData<Double> get() = _balance
//nam nam
private val _yearlyChartData = MutableLiveData<List<BarEntry>>()
    private val _yearlyLabels = MutableLiveData<List<String>>() // Nhãn T1, T2...
    private val _yearTotalIncome = MutableLiveData<Double>()
    private val _yearTotalExpense = MutableLiveData<Double>()
    private val _yearBalance = MutableLiveData<Double>()

    val yearlyChartData: LiveData<List<BarEntry>> get() = _yearlyChartData
    val yearlyLabels: LiveData<List<String>> get() = _yearlyLabels
    val yearTotalIncome: LiveData<Double> get() = _yearTotalIncome
    val yearTotalExpense: LiveData<Double> get() = _yearTotalExpense
    val yearBalance: LiveData<Double> get() = _yearBalance
    init {
        loadMockData()
        loadYearlyMockData()
    }

    // Hàm tạo dữ liệu giả để test biểu đồ
    private fun loadMockData() {
        // 1. Tạo danh sách cột (X: index, Y: số tiền)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 500000f))  // Ăn uống
        entries.add(BarEntry(1f, 200000f))  // Đi lại
        entries.add(BarEntry(2f, 1500000f)) // Tiền nhà
        entries.add(BarEntry(3f, 300000f))  // Mua sắm

        _chartData.value = entries

        // 2. Tạo nhãn tương ứng trục X
        val labels = listOf("Ăn uống", "Đi lại", "Tiền nhà", "Mua sắm")
        _categoryLabels.value = labels

        // 3. Tính tổng
        val expense = 500000 + 200000 + 1500000 + 300000.0
        val income = 5000000.0 // Giả sử thu nhập

        _totalIncome.value = income
        _totalExpense.value = expense
        _balance.value = income - expense
    }
    private fun loadYearlyMockData() {
        // 1. Tạo dữ liệu 12 tháng (Ví dụ: Trục X là tháng 0->11, Trục Y là tiền)
        val entries = ArrayList<BarEntry>()
        // Giả sử dữ liệu chi tiêu các tháng biến động
        entries.add(BarEntry(0f, 2000000f))  // Tháng 1
        entries.add(BarEntry(1f, 4500000f))  // Tháng 2 (Tết chi nhiều)
        entries.add(BarEntry(2f, 1500000f))  // Tháng 3
        entries.add(BarEntry(3f, 2000000f))  // Tháng 4
        entries.add(BarEntry(4f, 3000000f))  // Tháng 5
        entries.add(BarEntry(5f, 1800000f))  // Tháng 6
        entries.add(BarEntry(6f, 2200000f))  // Tháng 7
        entries.add(BarEntry(7f, 1500000f))  // Tháng 8
        entries.add(BarEntry(8f, 4000000f))  // Tháng 9 (Nhập học)
        entries.add(BarEntry(9f, 2000000f))  // Tháng 10
        entries.add(BarEntry(10f, 1500000f)) // Tháng 11
        entries.add(BarEntry(11f, 5000000f)) // Tháng 12

        _yearlyChartData.value = entries

        // 2. Tạo nhãn cho trục X (T1 -> T12)
        val labels = listOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        _yearlyLabels.value = labels

        // 3. Tính tổng năm
        val totalExp = 31000000.0 // Tổng chi giả định
        val totalInc = 60000000.0 // Tổng thu giả định

        _yearTotalIncome.value = totalInc
        _yearTotalExpense.value = totalExp
        _yearBalance.value = totalInc - totalExp
    }
}