package com.example.appcuoiky.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcuoiky.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class MainViewModel : ViewModel() {

    // LiveData hiển thị UI
    private val _currentMonth = MutableLiveData<String>()
    private val _totalIncome = MutableLiveData<Double>()
    private val _totalExpense = MutableLiveData<Double>()
    private val _balance = MutableLiveData<Double>()
    private val _dayList = MutableLiveData<List<String>>()

    val currentMonth: LiveData<String> get() = _currentMonth
    val totalIncome: LiveData<Double> get() = _totalIncome
    val totalExpense: LiveData<Double> get() = _totalExpense
    val balance: LiveData<Double> get() = _balance
    val dayList: LiveData<List<String>> get() = _dayList

    // Lịch và Firebase
    private val calendar = Calendar.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Lưu trữ danh sách giao dịch của TOÀN BỘ tháng hiện tại để không phải load lại khi click ngày
    private var currentMonthTransactions = listOf<Transaction>()

    init {
        updateDateDisplay() // Load tháng hiện tại và dữ liệu ngay khi mở
    }

    // Hàm gọi khi đổi tháng (Next/Prev)
    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateDateDisplay()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateDateDisplay()
    }

    // 1. Cập nhật giao diện lịch và tải dữ liệu tháng đó
    private fun updateDateDisplay() {
        // --- Logic tạo lịch (giữ nguyên) ---
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

        // --- Logic mới: Tải dữ liệu Firebase cho tháng này ---
        loadDataFromFirestore(month, year)
    }

    // 2. Tải dữ liệu từ Firebase
    private fun loadDataFromFirestore(month: Int, year: Int) {
        val userId = "user_test_01" // ID dùng để test
        val monthYearString = String.format("/%02d/%d", month, year) // vd: "/12/2025"

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Transaction>()
                for (doc in documents) {
                    val trans = doc.toObject(Transaction::class.java)
                    // Chỉ lấy các giao dịch thuộc tháng/năm đang xem
                    if (trans.date.endsWith(monthYearString)) {
                        list.add(trans)
                    }
                }
                currentMonthTransactions = list

                // Mặc định khi mới chuyển tháng: Hiển thị tổng của CẢ THÁNG
                calculateTotals(list)
            }
            .addOnFailureListener {
                Log.e("MainViewModel", "Lỗi load data: ", it)
            }
    }

    // 3. Hàm xử lý khi người dùng Click vào một ngày
    fun selectDate(day: String) {
        if (day.isEmpty()) return

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        // Tạo chuỗi ngày cần lọc: vd "15/12/2025" (đảm bảo định dạng 2 chữ số dd/MM/yyyy)
        val selectedDateStr = String.format("%02d/%02d/%d", day.toInt(), month, year)

        // Lọc trong danh sách đã tải về
        val dailyTransactions = currentMonthTransactions.filter { it.date == selectedDateStr }

        // Tính toán và hiển thị số liệu của NGÀY ĐÓ
        calculateTotals(dailyTransactions)
    }

    // Hàm phụ tính tổng và update LiveData
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