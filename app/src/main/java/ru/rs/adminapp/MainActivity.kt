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
import android.view.Menu
import android.view.MenuItem

/**
 * Created by Artem Botnev on 08/23/2018
 */
class MainActivity : AppCompatActivity(), PasswordDialog.Resolvable {
    companion object {
        private const val TAG = "MainActivity"

        fun launch(context: Context) =
                Intent(context, MainActivity::class.java)
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        .also { context.startActivity(it) }
    }

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    private lateinit var cameraButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = AdminReceiver.getComponentName(this)

        val isAdminActive = devicePolicyManager.isAdminActive(adminComponentName)
        if (savedInstanceState == null && !isAdminActive) askAdminRight()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        cameraButton = menu.getItem(0)

        cameraButton.setIcon(
                if (isCameraDisabled(this)) R.drawable.ic_cam_disable
                else R.drawable.ic_cam_enable)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.camera_button) {
            changeCameraStatus()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun doChange(enable: Boolean) {
        cameraButton.setIcon(
                if (enable) R.drawable.ic_cam_enable else R.drawable.ic_cam_disable)

        devicePolicyManager.setCameraDisabled(adminComponentName, !enable)
    }

    /**
     * Invoked by clicking on camera_button in menu
     */
    private fun changeCameraStatus() {
        val enableCamera = isCameraDisabled(this)
        checkPassword(enableCamera)
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
        PasswordDialog(this, attemptEnable)
                .showPasswordDialog(password == PASSWORD_DEFAULT_VAL)
                .show()
    }
}
