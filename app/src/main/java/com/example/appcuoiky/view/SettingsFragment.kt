package com.example.appcuoiky.view

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appcuoiky.R
import com.example.appcuoiky.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    private lateinit var tvUsername: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var imgAvatar: ImageView
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var layoutLanguage: LinearLayout
    private lateinit var tvCurrentLanguage: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnEditProfile: ImageView // Nút sửa mới

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        initViews(view)
        setupSettings() // Cài đặt trạng thái ban đầu cho Switch/Ngôn ngữ
        observeData()
        handleEvents()

        return view
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tvUsername)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        layoutLanguage = view.findViewById(R.id.layoutLanguage)
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
    }

    private fun setupSettings() {
        // 1. Kiểm tra Dark Mode hiện tại
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchDarkMode.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        // 2. Kiểm tra Ngôn ngữ hiện tại
        val currentLang = Locale.getDefault().language
        tvCurrentLanguage.text = if (currentLang == "en") "English" else "Tiếng Việt"
    }

    private fun observeData() {
        viewModel.username.observe(viewLifecycleOwner) { name -> tvUsername.text = name }
        viewModel.userEmail.observe(viewLifecycleOwner) { email -> tvUserEmail.text = email }
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            btnLogout.text = if (loggedIn) "Đăng xuất" else "Đăng nhập ngay"
        }
    }

    private fun handleEvents() {
        // 1. Xử lý Dark Mode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            // Không cần gọi recreate() thủ công, setDefaultNightMode thường tự xử lý
        }

        // 2. Xử lý đổi ngôn ngữ
        layoutLanguage.setOnClickListener {
            showLanguageDialog()
        }

        // 3. Xử lý sửa thông tin
        btnEditProfile.setOnClickListener {
            showEditDialog()
        }

        // 4. Xử lý đăng xuất
        btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }
    }

    // Hộp thoại chọn ngôn ngữ
    private fun showLanguageDialog() {
        val languages = arrayOf("Tiếng Việt", "English")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Chọn ngôn ngữ")
        builder.setItems(languages) { dialog, which ->
            if (which == 0) {
                setAppLocale("vi")
            } else {
                setAppLocale("en")
            }
            dialog.dismiss()
        }
        builder.show()
    }

    // Hàm đổi ngôn ngữ hệ thống và khởi động lại App
    private fun setAppLocale(localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        // Cập nhật cấu hình
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        // Load lại Activity để áp dụng ngôn ngữ mới
        requireActivity().recreate()
    }

    // Hộp thoại sửa tên
    private fun showEditDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cập nhật thông tin")

        // Tạo EditText trong Dialog
        val input = EditText(requireContext())
        input.hint = "Nhập tên mới"
        input.setText(tvUsername.text) // Điền sẵn tên cũ
        builder.setView(input)

        builder.setPositiveButton("Lưu") { dialog, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                viewModel.updateUserInfo(newName)
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}