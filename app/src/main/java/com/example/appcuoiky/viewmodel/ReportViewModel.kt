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

    // ================== PHẦN BÁO CÁO NĂM ==================
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
        // Mặc định khi vào sẽ load dữ liệu tháng và năm hiện tại, tab CHI
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        loadDataFromFirestore(currentMonth, currentYear, "CHI")
        loadYearlyDataFromFirestore(currentYear, "CHI")
    }

    // ================== 1. XỬ LÝ DỮ LIỆU THÁNG ==================
    fun loadDataFromFirestore(month: Int, year: Int, type: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) return
        val userId = currentUser.uid

        val monthYearString = String.format("/%02d/%d", month, year)

        // 1. Query dữ liệu cho biểu đồ
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
                Log.e("ReportViewModel", "Lỗi load biểu đồ tháng: ", it)
            }
    }

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

    // ================== 2. XỬ LÝ DỮ LIỆU NĂM ==================
    fun loadYearlyDataFromFirestore(year: Int, type: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) return
        val userId = currentUser.uid

        val yearString = "/$year" // Ví dụ: "/2025"

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { documents ->
                val entries = ArrayList<BarEntry>()
                // Mảng chứa tổng tiền 12 tháng (index 0 = Tháng 1, index 11 = Tháng 12)
                val monthlyTotals = FloatArray(12) { 0f }

                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    // Kiểm tra xem transaction có thuộc năm này không
                    if (trans.date.endsWith(yearString)) {
                        try {
                            // Cắt chuỗi ngày "dd/MM/yyyy" để lấy tháng (MM)
                            // Vị trí: 01/34/6789 -> index 3,4 là tháng
                            val monthStr = trans.date.substring(3, 5)
                            val monthIndex = monthStr.toInt() - 1 // Tháng 1 -> index 0

                            if (monthIndex in 0..11) {
                                monthlyTotals[monthIndex] += trans.amount.toFloat()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // Chuyển mảng thành dữ liệu biểu đồ
                for (i in 0 until 12) {
                    entries.add(BarEntry((i + 1).toFloat(), monthlyTotals[i]))
                }

                _yearlyChartData.value = entries

                // Gán nhãn trục X: T1, T2... T12
                val labels = (1..12).map { "T$it" }
                _yearlyLabels.value = labels

                // Tính toán tổng Thu/Chi/Số dư của cả năm
                calculateYearlyTotals(userId, yearString)
            }
            .addOnFailureListener {
                Log.e("ReportViewModel", "Lỗi load biểu đồ năm: ", it)
            }
    }

    private fun calculateYearlyTotals(userId: String, yearString: String) {
        // Tính tổng CHI cả năm
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", "CHI")
            .get()
            .addOnSuccessListener { chiDocs ->
                var totalChi = 0.0
                for (doc in chiDocs) {
                    val t = doc.toObject(Transaction::class.java)
                    if (t.date.endsWith(yearString)) totalChi += t.amount
                }
                _yearTotalExpense.value = totalChi

                // Tính tổng THU cả năm
                db.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("type", "THU")
                    .get()
                    .addOnSuccessListener { thuDocs ->
                        var totalThu = 0.0
                        for (doc in thuDocs) {
                            val t = doc.toObject(Transaction::class.java)
                            if (t.date.endsWith(yearString)) totalThu += t.amount
                        }
                        _yearTotalIncome.value = totalThu
                        _yearBalance.value = totalThu - totalChi
                    }
            }
    }
}