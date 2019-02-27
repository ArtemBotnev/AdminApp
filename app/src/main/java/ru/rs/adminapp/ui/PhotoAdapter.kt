package ru.rs.adminapp.ui

import android.net.Uri
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.squareup.picasso.Picasso
import ru.rs.adminapp.R
import ru.rs.adminapp.utils.FILE_PROVIDER
import ru.rs.adminapp.utils.deleteFile
import ru.rs.adminapp.utils.getImageFileIfExist

class PhotoAdapter(val listener: PhotoActionListener)
    : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    companion object {
        //span count for grid layout manager
        const val SPAN_COUNT = 3
        private const val TAG = "PhotoAdapter"
    }

    private val size = SPAN_COUNT * (30 + SPAN_COUNT)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PhotoHolder(LayoutInflater.from(parent.context), parent)

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
//        holders[position] = holder
    }

    override fun getItemCount() = size

    override fun getItemId(position: Int) = position.toLong()

//    override fun getItemViewType(position: Int) = position

    fun addPhoto(position: Int, uri: Uri) {
        Log.i(TAG, "Added photo with position: $position, uri: $uri")
    }

    fun deletePhoto(position: Int) {
        Log.i(TAG, "deleted photo with position: $position")
    }


    /**
     * Holder
     */
    inner class PhotoHolder(inflater: LayoutInflater, private val parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.photo_cell, parent, false)),
            View.OnClickListener {

        var imageButton: ImageButton
            private set

        private var photoUri: Uri? = null

        init {
            imageButton = itemView.findViewById<ImageButton>(R.id.take_photo)
                    .also { it.setOnClickListener(this) }

            loadPhoto()
        }

        override fun onClick(view: View?) {
            listener.clickOnItemWithPosition(adapterPosition)
        }

        private fun loadPhoto() {
            val photoFile = getImageFileIfExist(adapterPosition)

            photoFile?.let {
                val uri = FileProvider.getUriForFile(parent.context, FILE_PROVIDER, it)

                Picasso.get()
                        .load(uri)
                        .resize(imageButton.width, imageButton.height)
                        .centerCrop()
                        .into(imageButton)
            }
        }

//        fun loadPhoto(uri: Uri) {
//            photoUri = uri
//
//            Picasso.get()
//                    .load(photoUri)
//                    .resize(imageButton.width, imageButton.height)
//                    .centerCrop()
//                    .into(imageButton)
//        }

        fun deletePhoto() {
            if (photoUri == null) return

            deleteFile(adapterPosition)
            photoUri = null

            imageButton.setImageDrawable(parent.context.getDrawable(R.drawable.ic_take_photo))

            notifyItemChanged(adapterPosition)
        }
    }

    interface PhotoActionListener {
        fun clickOnItemWithPosition(position: Int)
    }
}