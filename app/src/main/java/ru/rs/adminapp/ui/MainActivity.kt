package ru.rs.adminapp.ui

import android.Manifest.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.GridLayout
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*

import ru.rs.adminapp.*
import ru.rs.adminapp.ui.PhotoAdapter.Companion.SPAN_COUNT
import ru.rs.adminapp.utils.*

import java.io.IOException

/**
 * Created by Artem Botnev on 08/23/2018
 */
class MainActivity : AppCompatActivity(),
        PasswordDialog.Resolvable, PhotoAdapter.PhotoActionListener {

    companion object {
        const val DELETE_PHOTO_CODE = 176

        private const val TAG = "MainActivity"
        private const val PHOTO_CELL_ID = "MainActivity.photoCellId"

        private const val REQUEST_PERMISSIONS_CODE = 174
        private const val REQUEST_CAMERA_CODE = 175

        fun launch(context: Context) =
                Intent(context, MainActivity::class.java)
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        .also { context.startActivity(it) }
    }

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    private lateinit var cameraButton: MenuItem

    private var imageFileURI: Uri? = null

    private var currentPhotoCellId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentPhotoCellId = savedInstanceState?.getInt(PHOTO_CELL_ID, 0) ?: 0

        devicePolicyManager = getSystemService(Activity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = AdminReceiver.getComponentName(this)

        adjustRecycler()

        val isAdminActive = devicePolicyManager.isAdminActive(adminComponentName)
        if (savedInstanceState == null && !isAdminActive) askAdminRight()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(PHOTO_CELL_ID, currentPhotoCellId)
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

                if (grantResults.isNotEmpty()) {
                    // when both permission is granted - capture photo
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        capturePhoto()
                    } else {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            showLongToast(this, R.string.camera_permission_not_granted)
                        }
                        if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                            showLongToast(this, R.string.external_storage_permission_not_granted)
                        }
                    }

                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            REQUEST_CAMERA_CODE -> loadPhotoToCell()
            DELETE_PHOTO_CODE -> deletePhoto()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun doChange(enable: Boolean) {
        cameraButton.setIcon(
                if (enable) R.drawable.ic_cam_enable else R.drawable.ic_cam_disable)

        devicePolicyManager.setCameraDisabled(adminComponentName, !enable)
    }

    override fun clickOnItemWithPosition(position: Int) {
        currentPhotoCellId = position

        imageFileURI = getImageFileIfExist(position)?.let {
            FileProvider.getUriForFile(this, FILE_PROVIDER, it)
        }

        imageFileURI?.let {
            val intent = ShowPhotoActivity.createIntent(this, it)
            startActivityForResult(intent, MainActivity.DELETE_PHOTO_CODE)
        } ?: checkPermissionAndCapturePhoto()
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

    private fun adjustRecycler() = recycler.run {
        layoutManager =
                GridLayoutManager(this@MainActivity, SPAN_COUNT, GridLayout.VERTICAL, false)

        adapter = PhotoAdapter(this@MainActivity)
    }

    private fun checkPermissionAndCapturePhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) capturePhoto()

        if (!isPermissionGranted(permission.CAMERA) ||
                !isPermissionGranted(permission.WRITE_EXTERNAL_STORAGE)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permission.CAMERA)) {
                showLongToast(this, R.string.camera_permission_explanation)
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permission.WRITE_EXTERNAL_STORAGE)) {
                showLongToast(this, R.string.external_storage_permission_explanation)
            }

            ActivityCompat.requestPermissions(this,
                    arrayOf(permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS_CODE)
        } else {
            capturePhoto()
        }
    }

    private fun isPermissionGranted(permission: String) =
            ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED

    private fun capturePhoto() {
        if (isCameraDisabled(this)) {
            showLongToast(this, R.string.camera_is_disable_explanation)

            return
        }

        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (photoIntent.resolveActivity(packageManager) != null) {
            val file = try {
                createImageFile(currentPhotoCellId)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return
            }

            val photoURI = FileProvider.getUriForFile(this, FILE_PROVIDER, file)

            imageFileURI = photoURI

            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(photoIntent, REQUEST_CAMERA_CODE)
        }
    }

    private fun loadPhotoToCell() {
        imageFileURI?.let {
            (recycler.adapter as PhotoAdapter).addPhoto(currentPhotoCellId, it)
        }
    }

    private fun deletePhoto() {
        deleteFile(currentPhotoCellId)
        (recycler.adapter as PhotoAdapter).deletePhoto(currentPhotoCellId)
    }
}
