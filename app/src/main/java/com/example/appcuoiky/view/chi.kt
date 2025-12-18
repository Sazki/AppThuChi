package com.example.appcuoiky.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.adapter.adapterdanhmuc
import com.example.appcuoiky.viewmodel.danhmuc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class chi : Fragment() {

    private lateinit var tv_date: TextView
    private lateinit var btn_next: ImageView
    private lateinit var btn_prev: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var edtAmount: EditText
    private lateinit var edtNote: EditText
    private lateinit var btnSave: Button

    private lateinit var adapter: adapterdanhmuc

    private var userId: String? = null
    private var userName: String? = null

    private var selectedCategoryName: String? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val list = mutableListOf<danhmuc>()

    private var categoriesListener: ListenerRegistration? = null

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("userId")
        userName = arguments?.getString("name")

        if (userId == null) {
            userId = auth.currentUser?.uid
        }
        android.util.Log.d("ChiFragment", "onCreate - userId: $userId, userName: $userName")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_date = view.findViewById(R.id.tv_date)
        btn_prev = view.findViewById(R.id.btn_prev)
        btn_next = view.findViewById(R.id.btn_next)
        recyclerView = view.findViewById(R.id.rvdanhmuc)

        edtAmount = view.findViewById(R.id.edtAmount)
        edtNote = view.findViewById(R.id.edtNote)
        btnSave = view.findViewById(R.id.btnSave)

        if (userName != null) {
        }

        updateDate()

        btn_prev.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDate()
        }

        btn_next.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDate()
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        adapter = adapterdanhmuc(
            list,
            onSelected = { selected ->
                selectedCategoryName = selected.name
            },
            onAddClicked = {
                showAddCategoryDialog()
            }
        )

        recyclerView.adapter = adapter

        loadCategoriesFromFirestore()

        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        // Kiểm tra userId
        if (userId == null) {
            Toast.makeText(requireContext(), "Lỗi: Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            return
        }

        val amountStr = edtAmount.text.toString().trim()
        val note = edtNote.text.toString().trim()

        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategoryName == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn một danh mục!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val amount = amountStr.toDouble()

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = sdf.format(calendar.time)

            val transId = UUID.randomUUID().toString()

            val transactionData = hashMapOf(
                "id" to transId,
                "userId" to userId!!,
                "content" to selectedCategoryName!!,
                "amount" to amount,
                "type" to "CHI",
                "date" to dateString,
                "note" to note
            )

            firestore.collection("transactions")
                .document(transId)
                .set(transactionData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "✅ Đã lưu khoản chi thành công!", Toast.LENGTH_SHORT).show()
                    clearInputs()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "❌ Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        edtAmount.setText("")
        edtNote.setText("")
    }

    private fun updateDate() {
        val format = SimpleDateFormat("dd/MM/yyyy (EEE)", Locale("vi", "VN"))
        tv_date.text = format.format(calendar.time)
    }

    private fun showAddCategoryDialog() {
        if (userId == null || userId!!.isEmpty()) {
            Toast.makeText(requireContext(), "Chưa đăng nhập, không thể thêm danh mục", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = android.app.AlertDialog.Builder(requireContext())
        val input = android.widget.EditText(requireContext())
        input.hint = "Nhập tên danh mục"
        dialog.setTitle("Thêm danh mục mới")
        dialog.setView(input)

        dialog.setPositiveButton("Thêm") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                val categoryId = UUID.randomUUID().toString()

                val newCategory = hashMapOf(
                    "content" to categoryId,
                    "icon" to "mn",
                    "name" to name,
                    "userId" to userId!!,
                    "type" to "CHI"
                )

                firestore.collection("category")
                    .document(categoryId)
                    .set(newCategory)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "✅ Đã thêm danh mục: $name", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "❌ Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Hủy", null)
        dialog.show()
    }

    private fun loadCategoriesFromFirestore() {
        categoriesListener = firestore.collection("category")
            .whereEqualTo("type", "CHI")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    list.clear()
                    val defaultCategories = mutableListOf<danhmuc>()
                    val userCategories = mutableListOf<danhmuc>()

                    for (doc in snapshot.documents) {
                        try {
                            val icon = doc.getString("icon") ?: ""
                            val name = doc.getString("name") ?: ""
                            val docUserId = doc.getString("userId") ?: ""
                            val iconRes = getIconResource(icon)

                            val categoryItem = danhmuc(
                                id = 0,
                                name = name,
                                iconRes = iconRes
                            )

                            if (docUserId.isEmpty()) {
                                defaultCategories.add(categoryItem)
                            } else if (docUserId == userId) {
                                userCategories.add(categoryItem)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    list.addAll(defaultCategories)
                    list.addAll(userCategories)

                    list.forEachIndexed { idx, item ->
                        item.id = idx + 1
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoriesListener?.remove()
    }

    private fun getIconResource(iconName: String): Int {
        return try {
            val resourceId = resources.getIdentifier(
                iconName.lowercase(),
                "drawable",
                requireContext().packageName
            )
            if (resourceId != 0) {
                resourceId
            } else {
                R.drawable.mn
            }
        } catch (e: Exception) {
            R.drawable.mn
        }
    }

    companion object {
        fun newInstance(userId: String?, userName: String?): thu {
            val fragment = thu()
            val args = Bundle()
            args.putString("userId", userId)
            args.putString("name", userName)
            fragment.arguments = args
            return fragment
        }
    }
}