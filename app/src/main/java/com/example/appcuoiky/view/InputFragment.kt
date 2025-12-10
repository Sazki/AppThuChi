package com.example.appcuoiky.view
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appcuoiky.R

class InputFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Tạm thời return view trống hoặc tạo layout đơn giản sau
        return inflater.inflate(R.layout.fragment_input, container, false)
    }
}