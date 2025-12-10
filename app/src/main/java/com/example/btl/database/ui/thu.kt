package com.example.btl.database.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
//import com.example.btl.adapter.adapterdanhmuc
import com.example.btl.adapter.adapterdanhmucthu
import com.example.btl.database.viewmodel.danhmucthu
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class thu : Fragment() {

    private lateinit var tv_date: TextView
    private lateinit var btn_next: ImageView
    private lateinit var btn_prev: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapterdanhmucthu

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ánh xạ view
        tv_date = view.findViewById(R.id.tv_date1)
        btn_prev = view.findViewById(R.id.btn_prev1)
        btn_next = view.findViewById(R.id.btn_next1)
        recyclerView = view.findViewById(R.id.rvdanhmuc1)

        val calendar = Calendar.getInstance()

        fun updateDate() {
            val format = SimpleDateFormat("dd/MM/yyyy (EEE)", Locale("vi", "VN"))
            tv_date.text = format.format(calendar.time)
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

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val list = mutableListOf(
            danhmucthu(1, "Tiền lương", R.drawable.luong),
            danhmucthu(2, "Tiền phụ cấp", R.drawable.lon),
            danhmucthu(3, "Tiền thưởng", R.drawable.tc),
            danhmucthu(4, "Thu nhập phụ", R.drawable.tnkhac),
            danhmucthu(5, "Đầu tư", R.drawable.dt),
        )

        adapter = adapterdanhmucthu(list) { selected ->
            Toast.makeText(
                requireContext(),
                "Đã chọn: ${selected.name1}",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerView.adapter = adapter
    }
    }
