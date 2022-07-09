package com.example.materialtest

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.graphics.BitmapFactory

import android.os.Build




class MainActivity : AppCompatActivity() {
    val fruits: MutableList<Image> = mutableListOf(
//        Image("1.gif", "/storage/emulated/0/Pictures/1.gif")
    )

    fun addimage(name:String,url:String,describe:String){
        fruits.add(Image(name,url,describe))
    }
    val fruitList = ArrayList<Image>()

    fun isPicture(file: File): Boolean {
        if (!file.exists()) {
            return false
        }
        val options = BitmapFactory.Options()
        BitmapFactory.decodeFile(file.toString(), options)
        options.inJustDecodeBounds = true
        return when {
            options.outWidth != -1 && options.outHeight != -1 -> true
            else -> false
        }
    }


    fun ISFOLDER(path:String):Boolean{
        val file = File(path)
        return file.isDirectory
    }

    fun findallimage(path: String,dbHelper:MyDatabaseHelper){
        val db=dbHelper.writableDatabase
        val directory = File(path)
        val files=directory.list()
        for(file in files){
            if(!ISFOLDER(path+"/"+file)){
                if(isPicture(File(path+"/"+file)))
                    dbHelper.insertImage(db,file,path,"")
            }
            else findallimage(path+"/"+file,dbHelper)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        supportActionBar?.let {
//            it.setDisplayHomeAsUpEnabled(true)
//            it.setHomeAsUpIndicator(R.drawable.ic_menu)
//        }
//        navView.setCheckedItem(R.id.navCall)
//        navView.setNavigationItemSelectedListener {
//            drawerLayout.closeDrawers()
//            true
//        }
//        fab.setOnClickListener { view ->
//            view.showSnackbar("Data deleted", "Undo") {
//                "Data restored".showToast(this)
//            }
//        }

        //权限申请
        val PERMISSIONS = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val PERMISSION_CODE = 123

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this@MainActivity, PERMISSIONS, PERMISSION_CODE)
            }
        }




        //创建数据库
        val dbHelper = MyDatabaseHelper(this, "ImageStore.db", 5)
        val db=dbHelper.writableDatabase


        val sdCard:File = Environment.getExternalStorageDirectory()
        val directory_pictures = File(sdCard, "Pictures")
        Log.i( "dic","directory_pictures=$directory_pictures")
        if (!directory_pictures.exists()) {
            directory_pictures.mkdir()
            Log.i( "dic","create directory success!")
        }
        thread {
            findallimage(directory_pictures.path,dbHelper)//查找文件目录下的所有图片，并将其插入数据库中
        }

//        val path=directory_pictures.path
//        val images=directory_pictures.list()
//        for (image in images){
////            dbHelper.insertImage(db,image,path)
//            addimage(image,path)
//        }

        //查询数据库中所有图片信息
        val allimages=dbHelper.getAllImages(db)
        var i=1
        val cnt= allimages[0]!!.toInt()
        while (i<cnt){
            val name=allimages.get(i*4+1)
            val url=allimages.get(i*4+2)
            val describe=allimages.get(i*4+3)
            if (name != null && url!=null) {
                if(describe.isNullOrEmpty())
                    addimage(name,url,"")
                else
                    addimage(name,url,describe)
            }
            i++
        }



        //刷新Adapter
        initFruits()
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        val adapter = ImageAdapter(this, fruitList)
        recyclerView.adapter = adapter
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refreshFruits(adapter)
        }


    }

    private fun refreshFruits(adapter: ImageAdapter) {
        thread {
            Thread.sleep(2000)
            runOnUiThread {
                initFruits()
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun initFruits() {
        fruitList.clear()
        repeat(50) {
            val index = (0 until fruits.size).random()
            fruitList.add(fruits[index])
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.backup -> {
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show()
            }
            R.id.delete -> {
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show()
            }
            R.id.settings -> Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show()
        }
        return true
    }

}
