package com.example.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.registerForRequestPermissionResult(
    alertMessage: String,
    successGrant: () -> Unit = {},
): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            successGrant.invoke()
        } else {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setMessage(alertMessage)
                    setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + context.packageName)
                        startActivity(intent)
                    }
                    setNegativeButton(getString(R.string.not_now)) { _, _ ->
                    }
                }
                builder.create()
            }
            alertDialog?.show()
        }
    }
}

fun requestPermission(
    resultLauncher: ActivityResultLauncher<String>,
    requireContext: Context,
    permission: String,
    requireActivity: Activity,
    permissionGranted: () -> Unit = {},
) {
    when {
        ContextCompat.checkSelfPermission(
            requireContext, permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            permissionGranted.invoke()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity, permission
        ) -> {
            resultLauncher.launch(permission)
        }

        else -> {
            resultLauncher.launch(permission)
        }
    }
}
