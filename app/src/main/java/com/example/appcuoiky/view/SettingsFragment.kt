package com.example.appcuoiky.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var btnEditProfile: ImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                Toast.makeText(context, "Đang xử lý ảnh...", Toast.LENGTH_SHORT).show()

                viewModel.saveAvatarToFirestore(bitmap) {
                    Toast.makeText(context, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi chọn ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        initViews(view)
        setupSettings()
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
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchDarkMode.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        val currentLang = Locale.getDefault().language
        if (currentLang == "en") {
            tvCurrentLanguage.text = "English"
        } else {
            tvCurrentLanguage.text = "Tiếng Việt"
        }
    }

    private fun observeData() {
        viewModel.username.observe(viewLifecycleOwner) { name -> tvUsername.text = name }
        viewModel.userEmail.observe(viewLifecycleOwner) { email -> tvUserEmail.text = email }

        viewModel.avatarData.observe(viewLifecycleOwner) { base64String ->
            if (!base64String.isNullOrEmpty()) {
                try {
                    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imgAvatar.setImageBitmap(decodedBitmap)
                } catch (e: Exception) {
                    imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            } else {
                imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (loggedIn) {
                btnLogout.text = getString(R.string.logout) // Lấy text từ strings.xml
            } else {
                btnLogout.text = getString(R.string.login_now)
            }
        }
    }

    private fun handleEvents() {
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val prefs = requireActivity().getSharedPreferences("AppSetting", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("Is_Dark_Mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        layoutLanguage.setOnClickListener {
            showLanguageDialog()
        }

        imgAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnEditProfile.setOnClickListener {
            showEditDialog()
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(context, getString(R.string.logout), Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Tiếng Việt", "English")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.language)) // "Chọn ngôn ngữ"
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

    private fun setAppLocale(localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        val prefs = requireActivity().getSharedPreferences("AppSetting", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("My_Lang", localeCode)
        editor.apply()

        requireActivity().resources.updateConfiguration(
            config,
            requireActivity().resources.displayMetrics
        )

        requireActivity().recreate()
    }

    private fun showEditDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cập nhật tên")

        val input = EditText(requireContext())
        input.hint = "Nhập tên mới"
        input.setText(tvUsername.text)
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