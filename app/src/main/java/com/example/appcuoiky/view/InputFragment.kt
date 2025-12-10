package com.example.appcuoiky.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appcuoiky.R
import com.example.appcuoiky.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InputFragment : Fragment() {

    private lateinit var edtContent: EditText
    private lateinit var edtAmount: EditText
    private lateinit var edtNote: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnSave: Button

    // Kết nối Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        // 1. Ánh xạ View
        edtContent = view.findViewById(R.id.edtContent)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtNote = view.findViewById(R.id.edtNote)
        radioGroup = view.findViewById(R.id.radioGroupType)
        btnSave = view.findViewById(R.id.btnSave)

        // 2. Xử lý sự kiện bấm nút Lưu
        btnSave.setOnClickListener {
            saveTransaction()
        }

        return view
    }

    private fun saveTransaction() {
        val content = edtContent.text.toString()
        val amountString = edtAmount.text.toString()
        val note = edtNote.text.toString()

        // Kiểm tra nhập liệu
        if (content.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập nội dung và số tiền!", Toast.LENGTH_SHORT).show()
            return
        }

        // Xác định loại (Thu hay Chi)
        val selectedId = radioGroup.checkedRadioButtonId
        val type = if (selectedId == R.id.rbThu) "THU" else "CHI"

        // Lấy ngày hiện tại
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // --- QUAN TRỌNG: TẠM THỜI DÙNG ID CỨNG ĐỂ TEST ---
        // Sau này sẽ thay bằng: FirebaseAuth.getInstance().currentUser?.uid
        val fakeUserId = "user_test_01"

        // Tạo đối tượng Transaction
        val transaction = Transaction(
            id = "", // Để trống, Firestore sẽ tự sinh
            userId = fakeUserId,
            content = content,
            amount = amountString.toDouble(),
            type = type,
            date = currentDate,
            note = note
        )

        // Đẩy lên Firestore
        db.collection("transactions")
            .add(transaction)
            .addOnSuccessListener { documentReference ->
                // Cập nhật lại ID vào chính document đó để dễ quản lý sau này
                documentReference.update("id", documentReference.id)

                Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                clearInputs() // Xóa trắng form
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        edtContent.setText("")
        edtAmount.setText("")
        edtNote.setText("")
        edtContent.requestFocus()
    }
}