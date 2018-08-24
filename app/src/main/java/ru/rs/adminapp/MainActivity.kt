package ru.rs.adminapp

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Switch

import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Artem Botnev on 08/23/2018
 */
class MainActivity : AppCompatActivity(), PasswordDialog.Resolvable {
    companion object {
        private const val TAG = "MainActivity"

        fun launch(context: Context) =
                Intent(context, MainActivity::class.java).also { context.startActivity(it) }
    }

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = AdminReceiver.getComponentName(this)

        adjustLayout()

        val isAdminActive = devicePolicyManager.isAdminActive(adminComponentName)
        if (savedInstanceState == null && !isAdminActive) askAdminRight()
    }

    override fun doChange(enable: Boolean) {
        devicePolicyManager.setCameraDisabled(adminComponentName, !enable)
        cameraImage.setImageResource(
                if (enable) R.drawable.ic_cam_enable else R.drawable.ic_cam_disable)
    }

    /**
     * Invoked by clicking on cameraSwitch, activity_main layout
     */
    fun changeCameraStatus(view: View) {
        val switch = view as Switch

        checkPassword(switch.isChecked)

//        devicePolicyManager.setCameraDisabled(adminComponentName, !switch.isChecked)
//        cameraImage.setImageResource(
//                if (switch.isChecked) R.drawable.ic_cam_enable else R.drawable.ic_cam_disable)
    }

    private fun adjustLayout() {
        if (devicePolicyManager.getCameraDisabled(adminComponentName)) {
            cameraImage.setImageResource(R.drawable.ic_cam_disable)
            cameraSwitch.isChecked = false
        } else {
            cameraImage.setImageResource(R.drawable.ic_cam_enable)
            cameraSwitch.isChecked = true
        }
    }

    private fun askAdminRight() {
        Intent(ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(EXTRA_DEVICE_ADMIN, adminComponentName)
            putExtra(EXTRA_ADD_EXPLANATION, getString(R.string.explanation))
        }.also { startActivity(it) }

        finish()
    }

    private fun checkPassword(attemptEnable: Boolean) {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val password = sharedPreference.getString(PASSWORD, PASSWORD_DEFAULT_VAL)

        // create dialog for setting password if password hasn't establish yet
        val dialog = PasswordDialog(this, attemptEnable)
                .showPasswordDialog(password == PASSWORD_DEFAULT_VAL)

        dialog.show()
    }
}
