package ru.rs.adminapp

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context

/**
 * Util file
 *
 * Created by ArtemBotnev on 08/30/2018
 */
fun isCameraDisabled(context: Context): Boolean {
    val devicePolicyManager =
            context.getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponentName = AdminReceiver.getComponentName(context)

    return devicePolicyManager.getCameraDisabled(adminComponentName)
}