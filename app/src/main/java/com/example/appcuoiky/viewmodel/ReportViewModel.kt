package com.example.appcuoiky.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcuoiky.model.Transaction
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ReportViewModel : ViewModel() {

    // ================== PHẦN BÁO CÁO THÁNG ==================
    private val _chartData = MutableLiveData<List<BarEntry>>()
    private val _categoryLabels = MutableLiveData<List<String>>()

    private val _totalIncome = MutableLiveData<Double>()
    private val _totalExpense = MutableLiveData<Double>()
    private val _balance = MutableLiveData<Double>()

    val chartData: LiveData<List<BarEntry>> get() = _chartData
    val categoryLabels: LiveData<List<String>> get() = _categoryLabels
    val totalIncome: LiveData<Double> get() = _totalIncome
    val totalExpense: LiveData<Double> get() = _totalExpense
    val balance: LiveData<Double> get() = _balance

    // ================== PHẦN BÁO CÁO NĂM (Mock Data) ==================
    private val _yearlyChartData = MutableLiveData<List<BarEntry>>()
    private val _yearlyLabels = MutableLiveData<List<String>>()
    private val _yearTotalIncome = MutableLiveData<Double>()
    private val _yearTotalExpense = MutableLiveData<Double>()
    private val _yearBalance = MutableLiveData<Double>()

    val yearlyChartData: LiveData<List<BarEntry>> get() = _yearlyChartData
    val yearlyLabels: LiveData<List<String>> get() = _yearlyLabels
    val yearTotalIncome: LiveData<Double> get() = _yearTotalIncome
    val yearTotalExpense: LiveData<Double> get() = _yearTotalExpense
    val yearBalance: LiveData<Double> get() = _yearBalance

    private val db = FirebaseFirestore.getInstance()

    init {
        loadYearlyMockData()
        // Mặc định khi vào sẽ load dữ liệu tháng hiện tại, tab CHI
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        loadDataFromFirestore(currentMonth, currentYear, "CHI")
    }

    // Hàm load dữ liệu linh hoạt theo loại (type: "THU" hoặc "CHI")
    fun loadDataFromFirestore(month: Int, year: Int, type: String) {
        val userId = "user_test_01" // ID cố định để test
        val monthYearString = String.format("/%02d/%d", month, year)

        // 1. Query dữ liệu cho biểu đồ (Theo type được chọn)
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { documents ->
                val entries = ArrayList<BarEntry>()
                val labels = ArrayList<String>()
                val categoryMap = mutableMapOf<String, Double>()

                // Gộp tiền theo danh mục
                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    if (trans.date.endsWith(monthYearString)) {
                        val currentAmount = categoryMap.getOrDefault(trans.content, 0.0)
                        categoryMap[trans.content] = currentAmount + trans.amount
                    }
                }

                // Map sang BarEntry
                var index = 0f
                for ((categoryName, amount) in categoryMap) {
                    entries.add(BarEntry(index, amount.toFloat()))
                    labels.add(categoryName)
                    index++
                }

                _chartData.value = entries
                _categoryLabels.value = labels

                // Sau khi load xong biểu đồ, gọi hàm tính tổng thu/chi toàn tháng để cập nhật số dư
                calculateMonthlyTotals(userId, monthYearString)
            }
            .addOnFailureListener {
                Log.e("ReportViewModel", "Lỗi load biểu đồ: ", it)
            }
    }

    // Hàm tính tổng Thu và Chi của tháng (độc lập với biểu đồ) để hiển thị số dư đúng
    private fun calculateMonthlyTotals(userId: String, monthYearString: String) {
        // Tính tổng CHI
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", "CHI")
            .get()
            .addOnSuccessListener { documents ->
                var expense = 0.0
                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    if (trans.date.endsWith(monthYearString)) expense += trans.amount
                }
                _totalExpense.value = expense

                // Tính tổng THU (lồng nhau để đảm bảo đồng bộ khi tính số dư)
                db.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("type", "THU")
                    .get()
                    .addOnSuccessListener { incomeDocs ->
                        var income = 0.0
                        for (doc in incomeDocs) {
                            val trans = doc.toObject(Transaction::class.java)
                            if (trans.date.endsWith(monthYearString)) income += trans.amount
                        }
                        _totalIncome.value = income
                        _balance.value = income - expense
                    }
            }
    }

    private fun loadYearlyMockData() {
        val entries = ArrayList<BarEntry>()
        // Mock data năm...
        entries.add(BarEntry(0f, 2000000f))
        // ... (Code cũ giữ nguyên)
        _yearlyChartData.value = entries
        _yearlyLabels.value = listOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        _yearTotalIncome.value = 60000000.0
        _yearTotalExpense.value = 31000000.0
        _yearBalance.value = 29000000.0
    }
}