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
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        loadDataFromFirestore(currentMonth, currentYear, "CHI")
        loadYearlyDataFromFirestore(currentYear, "CHI")
    }

    fun loadDataFromFirestore(month: Int, year: Int, type: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) return
        val userId = currentUser.uid

        val monthYearString = String.format("/%02d/%d", month, year)

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { documents ->
                val entries = ArrayList<BarEntry>()
                val labels = ArrayList<String>()
                val categoryMap = mutableMapOf<String, Double>()

                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    if (trans.date.endsWith(monthYearString)) {
                        val currentAmount = categoryMap.getOrDefault(trans.content, 0.0)
                        categoryMap[trans.content] = currentAmount + trans.amount
                    }
                }

                var index = 0f
                for ((categoryName, amount) in categoryMap) {
                    entries.add(BarEntry(index, amount.toFloat()))
                    labels.add(categoryName)
                    index++
                }

                _chartData.value = entries
                _categoryLabels.value = labels

                calculateMonthlyTotals(userId, monthYearString)
            }
            .addOnFailureListener {
                Log.e("ReportViewModel", "Lỗi load biểu đồ tháng: ", it)
            }
    }

    private fun calculateMonthlyTotals(userId: String, monthYearString: String) {
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
                val monthlyTotals = FloatArray(12) { 0f }

                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    if (trans.date.endsWith(yearString)) {
                        try {

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

                for (i in 0 until 12) {
                    entries.add(BarEntry(i.toFloat(), monthlyTotals[i]))
                }

                _yearlyChartData.value = entries

                val labels = (1..12).map { "T$it" }
                _yearlyLabels.value = labels
                calculateYearlyTotals(userId, yearString)
            }
            .addOnFailureListener {
                Log.e("ReportViewModel", "Lỗi load biểu đồ năm: ", it)
            }
    }

    private fun calculateYearlyTotals(userId: String, yearString: String) {
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