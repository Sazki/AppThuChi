package com.example.appcuoiky.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.appcuoiky.R
class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }, 2000)
    }
}



