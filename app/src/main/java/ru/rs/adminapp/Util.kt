package ru.rs.adminapp

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.widget.Toast

/**
 * Util file
 *
 * Created by Artem Botnev on 08/30/2018
 */
fun showLongToast(context: Context, sourceId: Int) =
        Toast.makeText(context, sourceId, Toast.LENGTH_LONG).show()

fun isCameraDisabled(context: Context): Boolean {
    val devicePolicyManager =
            context.getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponentName = AdminReceiver.getComponentName(context)

    return devicePolicyManager.getCameraDisabled(adminComponentName)
}