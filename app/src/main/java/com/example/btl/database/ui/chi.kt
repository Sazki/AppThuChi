package com.example.btl.database.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.adapter.adapterdanhmuc
import com.example.btl.database.viewmodel.danhmuc
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class chi : Fragment() {

        private lateinit var tv_date: TextView
        private lateinit var btn_next: ImageView
        private lateinit var btn_prev: ImageView
        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: adapterdanhmuc

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_chi, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // ánh xạ view
            tv_date = view.findViewById(R.id.tv_date)
            btn_prev = view.findViewById(R.id.btn_prev)
            btn_next = view.findViewById(R.id.btn_next)
            recyclerView = view.findViewById(R.id.rvdanhmuc)

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
                danhmuc(1, "Ăn uống", R.drawable.an),
                danhmuc(2, "Phí giao lưu", R.drawable.gluu),
                danhmuc(3, "Y tế", R.drawable.yte),
                danhmuc(4, "Mỹ phẩm", R.drawable.makeup),
                danhmuc(5, "Tiền điện", R.drawable.tdien),
                danhmuc(6, "Tiền nhà", R.drawable.tnha),
                danhmuc(7, "Chi tiêu khác", R.drawable.other),
                danhmuc(8, "Giáo dục", R.drawable.hoc),
                danhmuc(9, "Quần áo", R.drawable.ao)
            )

            adapter = adapterdanhmuc(list) { selected ->
                Toast.makeText(
                    requireContext(),
                    "Đã chọn: ${selected.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            recyclerView.adapter = adapter
        }
    }


