package ru.rs.adminapp

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val devicePolicyManager =
                    this.getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager

            val isAdminActive = devicePolicyManager
                    .isAdminActive(AdminReceiver.getComponentName(this@MainActivity))

            if (!isAdminActive) askAdminRight()
        }
    }

    private fun askAdminRight() =
            Intent(ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(EXTRA_DEVICE_ADMIN, AdminReceiver.getComponentName(this@MainActivity))
                putExtra(EXTRA_ADD_EXPLANATION, "Explanation")
            }.also { startActivity(it) }
}
