package com.example.appcuoiky.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class MainViewModel : ViewModel() {

    // 1. Khai báo các biến LiveData để lưu trữ dữ liệu
    // _ (underscore) là biến nội bộ, chỉ ViewModel chỉnh sửa được
    private val _currentMonth = MutableLiveData<String>()
    private val _totalIncome = MutableLiveData<Double>()
    private val _totalExpense = MutableLiveData<Double>()
    private val _balance = MutableLiveData<Double>()

    // Biến public để MainActivity chỉ được phép đọc (quan sát)
    val currentMonth: LiveData<String> get() = _currentMonth
    val totalIncome: LiveData<Double> get() = _totalIncome
    val totalExpense: LiveData<Double> get() = _totalExpense
    val balance: LiveData<Double> get() = _balance
// Phần lịch

    private val _dayList = MutableLiveData<List<String>>()
    val dayList: LiveData<List<String>> get() = _dayList

    // Dùng Calendar để xử lý logic tăng giảm tháng
    private val calendar = Calendar.getInstance()

    init {
        // Code trong này chạy ngay khi ViewModel được khởi tạo
        updateDateDisplay()

        // Khởi tạo giá trị ban đầu là 0
        _totalIncome.value = 0.0
        _totalExpense.value = 0.0
        updateBalance()
    }

    // Hàm tính toán số dư
    private fun updateBalance() {
        val income = _totalIncome.value ?: 0.0
        val expense = _totalExpense.value ?: 0.0
        _balance.value = income - expense
    }

    // Hàm cập nhật chuỗi hiển thị tháng (VD: "T12/2025")
    private fun updateDateDisplay() {
        // 1. Cập nhật Text Tháng
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        _currentMonth.value = "T$month/$year"

        // 2. Tính toán danh sách ngày trong tháng
        val daysInMonthArray = ArrayList<String>()

        // Copy lịch ra một bản clone để tính toán ngày đầu tháng
        val calendarCopy = calendar.clone() as Calendar
        calendarCopy.set(Calendar.DAY_OF_MONTH, 1) // Set về ngày mùng 1

        // Lấy thứ trong tuần của ngày mùng 1 (CN = 1, T2 = 2, ...)
        val dayOfWeek = calendarCopy.get(Calendar.DAY_OF_WEEK)

        // Tính số ô trống trước ngày mùng 1
        // Ví dụ: Mùng 1 là Thứ 4 -> Cần trống CN, T2, T3 (3 ô)
        val emptySlots = dayOfWeek - 1
        for (i in 0 until emptySlots) {
            daysInMonthArray.add("") // Thêm ô trống
        }

        // Lấy tổng số ngày của tháng hiện tại
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDays) {
            daysInMonthArray.add(i.toString())
        }

        // Đẩy danh sách ra View
        _dayList.value = daysInMonthArray
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateDateDisplay()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateDateDisplay()
    }
}