package ru.rs.adminapp

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context

fun isCameraDisabled(context: Context): Boolean {
    val devicePolicyManager =
            context.getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponentName = AdminReceiver.getComponentName(context)

    return devicePolicyManager.getCameraDisabled(adminComponentName)
}