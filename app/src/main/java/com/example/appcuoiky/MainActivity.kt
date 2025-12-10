package com.example.appcuoiky.database

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.btl.R
import com.example.btl.database.ui.dangki
import com.example.btl.database.ui.email
import com.example.btl.database.ui.quenmatkhau

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.login).setOnClickListener {
            openFragment(email())
        }
        findViewById<Button>(R.id.quen).setOnClickListener {
            openFragment(quenmatkhau())
        }

        findViewById<Button>(R.id.singin1).setOnClickListener {
            openFragment(dangki())
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FL1, fragment)
            .addToBackStack(null)
            .commit()
    }
}