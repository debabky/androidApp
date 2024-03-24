package org.freedomtool.feature.intro


import android.Manifest
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityStartBinding
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.nfc.PermissionUtil

class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)
        binding.lifecycleOwner = this

        initButton()
    }

    private fun initButton() {
        clickHelper.addViews(binding.start)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.start.id -> {

                    requestPermissionForCamera()
                }

            }
        }
    }

    private fun requestPermissionForCamera() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val isPermissionGranted = PermissionUtil.hasPermissions(this, *permissions)
        if (!isPermissionGranted) {

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.permission_title))
                .setMessage(resources.getString(R.string.permission_description))
                .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS
                    )
                }
                .show()


        } else {
            finish()
            Navigator.from(this).openScan()
        }
    }

}