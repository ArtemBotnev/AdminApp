package ru.rs.adminapp

import android.Manifest.*
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.GridLayout
import android.widget.ImageButton

import kotlinx.android.synthetic.main.activity_main.recycler

/**
 * Created by Artem Botnev on 08/23/2018
 */
class MainActivity : AppCompatActivity(), PasswordDialog.Resolvable {
    companion object {
        private const val TAG = "MainActivity"
        //span count for grid layout manager
        private const val SPAN_COUNT = 3

        private const val REQUEST_PERMISSIONS_CODE = 174

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

        adjustRecycler()

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: invoke camera capture
                } else {
                    showLongToast(this, R.string.camera_permission_not_granted)
                }
            }
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

    private fun adjustRecycler() = with(recycler) {
        layoutManager =
                GridLayoutManager(this@MainActivity, SPAN_COUNT, GridLayout.VERTICAL, false)

        adapter = PhotoAdapter()
    }

    private fun checkPermissionAndCapturePhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permission.CAMERA)) {
                showLongToast(this, R.string.camera_permission_explanation)
            }

            ActivityCompat.requestPermissions(this,
                    arrayOf(permission.CAMERA), REQUEST_PERMISSIONS_CODE)
        } else {
            //TODO: invoke camera capture
        }
    }

    /**
     * Inner classes for recycler
     */

    private inner class PhotoHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.photo_cell, parent, false)),
            View.OnClickListener {

        init {
            itemView.findViewById<ImageButton>(R.id.take_photo).setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            checkPermissionAndCapturePhoto()
        }

        fun bind() {

        }
    }

    private inner class PhotoAdapter : RecyclerView.Adapter<PhotoHolder>() {
        private val size = SPAN_COUNT * (30 + SPAN_COUNT)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                PhotoHolder(layoutInflater, parent)

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount() = size
    }
}
