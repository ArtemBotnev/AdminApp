package ru.rs.adminapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_show_photo.*

import ru.rs.adminapp.R

/**
 * Created by Artem Botnev on 09/02/2018
 */
class ShowPhotoActivity : AppCompatActivity() {
    companion object {
        private const val PHOTO_URI = "ShowPhotoActivity.PhotoUri"

        fun createIntent(context: Context, photoUri: Uri) =
                Intent(context, ShowPhotoActivity::class.java)
                        .apply { putExtra(PHOTO_URI, photoUri) }
    }

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)

        photoImage.viewTreeObserver.addOnGlobalLayoutListener { loadImage() }

        imageUri = savedInstanceState?.getParcelable(PHOTO_URI) ?:
                intent.getParcelableExtra(PHOTO_URI)
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(PHOTO_URI, imageUri)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_show_photo, menu)
        menu.getItem(0).apply { setIcon(R.drawable.ic_delete) }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.delete_button) {
            setResult(Activity.RESULT_OK)
            finish()

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loadImage() =
            Picasso.get()
                    .load(imageUri)
                    .resize(photoImage.width, photoImage.height)
                    .centerInside()
                    .into(photoImage)

}