package ru.rs.adminapp

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AdminReceiver : DeviceAdminReceiver() {
    companion object {
        const val TAG = "AdminReceiver"

        fun getComponentName(context: Context): ComponentName =
                ComponentName(context.applicationContext, AdminReceiver::class.java)
    }

    override fun onEnabled(context: Context?, intent: Intent?) {
        Log.i(TAG, "Enabled")
        showToast(context!!, R.string.app_enable)

        MainActivity.launch(context)

        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        Log.i(TAG, "Disabled")
        showToast(context!!, R.string.app_disable)
        super.onDisabled(context, intent)
    }

    private fun showToast(context: Context, sourceId: Int) =
            Toast.makeText(context, sourceId, Toast.LENGTH_SHORT).show()
}