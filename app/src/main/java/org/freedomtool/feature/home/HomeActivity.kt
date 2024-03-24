package org.freedomtool.feature.home

import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityHomeBinding
import org.freedomtool.feature.scanQR.ScanQrCameraActivity
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.decodeHexString
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG1File


class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, org.freedomtool.R.layout.activity_home)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(org.freedomtool.R.color.primary_button_color)
        Color.valueOf(168.0f, 144.0f, 254.0f, 0.89f)

        initView()
        initButton()
    }

    private fun initButton() {
        clickHelper.addViews(binding.qrCodeBtn, binding.exit)
        clickHelper.setOnClickListener {
            when(it.id) {
                binding.qrCodeBtn.id -> {
                    val intent = Intent(this, ScanQrCameraActivity::class.java)
                    barcodeLauncher.launch(intent)
                }

                binding.exit.id -> {
                    clearAllData()
                }
            }
        }
    }

    private fun clearAllData() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(org.freedomtool.R.string.delete_all_data_header))
            .setMessage(resources.getString(org.freedomtool.R.string.delete_all_data_message))
            .setPositiveButton(resources.getString(org.freedomtool.R.string.button_ok)) { _, _ ->
                compositeDisposable.clear()
                SecureSharedPrefs.clearAllData(this)
                Navigator.from(this).openStart()
                finish()
            }
            .setNegativeButton(resources.getString(org.freedomtool.R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private val barcodeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val res = result.data!!.getStringExtra(ScanQrCameraActivity.RESULT_CODE)
                    toastManager.long(res)
                }
            }
        }

    private fun initView() {
        val photo = SecureSharedPrefs.getDG2(this)
        val imageAsBytes: ByteArray = Base64.decode(photo!!.toByteArray(), Base64.DEFAULT)
        binding.appCompatImageView18.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))

        val dg1 = SecureSharedPrefs.getDG1(this)!!
        val mrz = DG1File(dg1.decodeHexString().inputStream()).mrzInfo

        binding.materialTextView30.text = mrz.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
        binding.sername.text = mrz.primaryIdentifier.replace("<", " ").trim { it <= ' ' }

        binding.materialTextView28.text = mrz.dateOfBirth.chunked(2).reversed().joinToString(".")
        binding.exparisy.text = mrz.dateOfExpiry.chunked(2).reversed().joinToString(".")

        binding.nationalitytext.text = mrz.nationality
    }

}