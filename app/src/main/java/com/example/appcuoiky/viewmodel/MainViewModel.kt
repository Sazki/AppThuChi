package com.example.appcuoiky.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcuoiky.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class MainViewModel : ViewModel() {

    private val _currentMonth = MutableLiveData<String>()
    private val _totalIncome = MutableLiveData<Double>()
    private val _totalExpense = MutableLiveData<Double>()
    private val _balance = MutableLiveData<Double>()
    private val _dayList = MutableLiveData<List<String>>()
    private val _selectedDateTransactions = MutableLiveData<List<Transaction>>()
    private val _selectedDate = MutableLiveData<String>()

    val currentMonth: LiveData<String> get() = _currentMonth
    val totalIncome: LiveData<Double> get() = _totalIncome
    val totalExpense: LiveData<Double> get() = _totalExpense
    val balance: LiveData<Double> get() = _balance
    val dayList: LiveData<List<String>> get() = _dayList
    val selectedDateTransactions: LiveData<List<Transaction>> = _selectedDateTransactions
    val selectedDate: LiveData<String> = _selectedDate

    private val calendar = Calendar.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var currentMonthTransactions = listOf<Transaction>()

    init {
        updateDateDisplay()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateDateDisplay()
        clearSelectedDate()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateDateDisplay()
        clearSelectedDate()
    }

    private fun updateDateDisplay() {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        _currentMonth.value = "T$month/$year"

        val daysInMonthArray = ArrayList<String>()
        val calendarCopy = calendar.clone() as Calendar
        calendarCopy.set(Calendar.DAY_OF_MONTH, 1)
        val dayOfWeek = calendarCopy.get(Calendar.DAY_OF_WEEK)
        val emptySlots = dayOfWeek - 1
        for (i in 0 until emptySlots) daysInMonthArray.add("")
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDays) daysInMonthArray.add(i.toString())
        _dayList.value = daysInMonthArray

        loadDataFromFirestore(month, year)
    }

    private fun loadDataFromFirestore(month: Int, year: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            currentMonthTransactions = emptyList()
            calculateTotals(emptyList())
            return
        }
        val userId = currentUser.uid
        val monthYearString = String.format("/%02d/%d", month, year)

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Transaction>()
                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    if (trans.date.endsWith(monthYearString)) {
                        list.add(trans)
                    }
                }
                currentMonthTransactions = list
                calculateTotals(list)
            }
            .addOnFailureListener {
                Log.e("MainViewModel", "Lỗi load data: ", it)
                currentMonthTransactions = emptyList()
                calculateTotals(emptyList())
            }
    }

    fun selectDate(day: String) {
        if (day.isEmpty()) {
            clearSelectedDate()
            return
        }

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val selectedDateStr = String.format("%02d/%02d/%d", day.toInt(), month, year)

        _selectedDate.value = selectedDateStr

        val dailyTransactions = currentMonthTransactions.filter { it.date == selectedDateStr }
        _selectedDateTransactions.value = dailyTransactions

        calculateTotals(dailyTransactions)
    }

    fun clearSelectedDate() {
        _selectedDate.value = ""
        _selectedDateTransactions.value = emptyList()
        calculateTotals(currentMonthTransactions)
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var inc = 0.0
        var exp = 0.0

        for (t in transactions) {
            if (t.type == "THU") inc += t.amount
            if (t.type == "CHI") exp += t.amount
        }

        _totalIncome.value = inc
        _totalExpense.value = exp
        _balance.value = inc - exp
    }
}