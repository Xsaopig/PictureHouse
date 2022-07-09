package com.example.materialtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_fruit.*
import android.view.MenuItem

class ImageActivity : AppCompatActivity() {

    companion object {
        const val FRUIT_NAME = "fruit_name"
        const val FRUIT_IMAGE_ID = "fruit_image_id"
        const val FRUIT_DESCRIBE = "fruit_describe"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit)
        val ImageName = intent.getStringExtra(FRUIT_NAME) ?: ""
        val url = intent.getStringExtra(FRUIT_IMAGE_ID)
        var Describe = intent.getStringExtra(FRUIT_DESCRIBE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsingToolbar.title = ImageName
        Glide.with(this).load(url+"/"+ImageName).into(fruitImageView)
//        fruitContentText.text = generateFruitContent(fruitDescribe)
        fruitContentText.setText(Describe)
        val dbHelper = MyDatabaseHelper(this, "ImageStore.db", 5)
        val db=dbHelper.writableDatabase
        commit.setOnClickListener {
            Describe= fruitContentText.text.toString()
            dbHelper.updateImageDescribe(db,ImageName,url,Describe)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun generateFruitContent(fruitDescribe:String): Editable? = fruitDescribe

}
