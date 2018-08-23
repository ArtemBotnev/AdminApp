package ru.rs.adminapp

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class AdminReceiver : DeviceAdminReceiver() {
    companion object {
        val TAG = "AdminReceiver"

        fun getComponentName(context: Context): ComponentName =
                ComponentName(context.applicationContext, AdminReceiver::class.java)
    }

    override fun onEnabled(context: Context?, intent: Intent?) {
        Log.i(TAG, "Enabled")
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        Log.i(TAG, "Disabled")
        super.onDisabled(context, intent)
    }
}