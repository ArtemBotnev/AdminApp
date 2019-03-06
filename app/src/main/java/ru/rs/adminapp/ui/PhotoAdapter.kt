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
import ru.rs.adminapp.utils.getImageFileIfExist

/**
 * Created by Artem Botnev on 02/2019
 */
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
        holder.loadPhoto()
    }

    override fun getItemCount() = size

    override fun getItemId(position: Int) = position.toLong()

    fun addPhoto(position: Int, uri: Uri) {
        Log.i(TAG, "Added photo with position: $position, uri: $uri")
        notifyItemChanged(position)
    }

    fun deletePhoto(position: Int) {
        Log.i(TAG, "deleted photo with position: $position")
        notifyItemChanged(position)
    }


    /**
     * Holder
     */
    inner class PhotoHolder(inflater: LayoutInflater, private val parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.photo_cell, parent, false)),
            View.OnClickListener {

        private val imageButton: ImageButton = itemView
                .findViewById<ImageButton>(R.id.take_photo).also { it.setOnClickListener(this) }

        private var imageHeight = 0
        private var imageWight = 0

        override fun onClick(view: View?) {
            listener.clickOnItemWithPosition(adapterPosition)
        }

        init {
            // get size of image view
            imageButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            imageHeight = imageButton.measuredHeight
            imageWight = imageButton.measuredWidth
        }

        fun loadPhoto() {
            val photoFile = getImageFileIfExist(adapterPosition)

            photoFile?.let {
                val uri = FileProvider.getUriForFile(parent.context, FILE_PROVIDER, it)

                Picasso.get()
                        .load(uri)
                        .resize(imageWight, imageHeight)
                        .centerCrop()
                        .into(imageButton)

            } ?: imageButton.setImageDrawable(parent.context.getDrawable(R.drawable.ic_take_photo))
        }
    }

    interface PhotoActionListener {
        fun clickOnItemWithPosition(position: Int)
    }
}