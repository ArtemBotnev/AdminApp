package ru.rs.adminapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_show_photo.*

import ru.rs.adminapp.R
import ru.rs.adminapp.utils.FILE_PROVIDER
import ru.rs.adminapp.utils.getImageFileIfExist

class ShowPhotoActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_ID = "image_id"
        const val IMAGE_DELETED = 1

        fun createIntent(context: Context, cellId: Int) =
                Intent(context, ShowPhotoActivity::class.java)
                        .apply { putExtra(IMAGE_ID, cellId) }
    }

    private var imageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)

        imageId = savedInstanceState?.getInt(IMAGE_ID, 0) ?:
                intent.getIntExtra(IMAGE_ID, 0)
    }

    override fun onStart() {
        super.onStart()

        val file = getImageFileIfExist(imageId)
        if (file == null) {
            finish()
            return
        }

        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)

        Picasso.get()
                .load(uri)
                .resize(photoImage.width, photoImage.height)
                .centerCrop()
                .into(photoImage)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(IMAGE_ID, imageId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_show_photo, menu)
        menu.getItem(0).apply { setIcon(R.drawable.ic_delete) }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.delete_button) {

            Intent().apply { putExtra("result", IMAGE_DELETED) }
                    .also { setResult(Activity.RESULT_OK, it) }

            finish()

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}