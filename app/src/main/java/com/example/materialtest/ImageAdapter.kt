package com.example.materialtest

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(val context: Context, val imageList: List<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fruitImage: ImageView = view.findViewById(R.id.fruitImage)
        val fruitName: TextView = view.findViewById(R.id.fruitName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(context).inflate(R.layout.fruit_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {

            val position = holder.adapterPosition
            val fruit = imageList[position]

            val dbHelper = MyDatabaseHelper(context, "ImageStore.db", 5)
            val db=dbHelper.writableDatabase
            fruit.describe=dbHelper.queryDescribe(db,fruit.name,fruit.imageId)

            val intent = Intent(context, ImageActivity::class.java).apply {
                putExtra(ImageActivity.FRUIT_NAME, fruit.name)
                putExtra(ImageActivity.FRUIT_IMAGE_ID, fruit.imageId)
                putExtra(ImageActivity.FRUIT_DESCRIBE, fruit.describe)
            }
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fruit = imageList[position]
        holder.fruitName.text = fruit.name
        Glide.with(context).load(fruit.imageId+"/"+fruit.name).into(holder.fruitImage);
    }

    override fun getItemCount() = imageList.size

}