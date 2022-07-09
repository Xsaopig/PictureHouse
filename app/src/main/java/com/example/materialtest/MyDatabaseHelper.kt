package com.example.materialtest

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.ArrayList

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    private val dropImage="drop table Images;"
    private val createImage = "create table Images (" +
            " id integer primary key autoincrement," +
            "name text NOT NULL," +
            "url text NOT NULL," +
            "describe text)"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createImage)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    fun insertImage(db: SQLiteDatabase,name: String,url:String,describe:String?){
        if(query(db,name,url)==true) return
        val values = ContentValues().apply {
            put("name", name)
            put("url", url)
            put("describe",describe)
        }
        db.insert("Images", null, values) // 插入数据
    }

    fun query(db: SQLiteDatabase,name: String,url: String): Boolean {
        val cursor=db.query("Images", null,"name=? and url=?", arrayOf(name, url),null,null,null)
        var cnt=0
        if (cursor.moveToFirst()) {
            do {
                cnt += 1
            } while (cursor.moveToNext())
        }
        if(cnt==0) return false
        else return true
    }

    fun queryDescribe(db: SQLiteDatabase,name: String,url: String): String {
        val cursor=db.query("Images", null,"name=? and url=?", arrayOf(name, url),null,null,null)
        var describe=""
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getColumnIndex("describe")!=-1)
                    describe= cursor.getString(cursor.getColumnIndex("describe"))
            } while (cursor.moveToNext())
        }
        return describe
    }

    fun updateImageDescribe(db: SQLiteDatabase,name: String,url: String,describe: String){
        val values = ContentValues()
        values.put("describe",describe)
        val bool=db.update("Images", values, "name = ? and url=?", arrayOf(name,url))
    }

    fun getAllImages(db: SQLiteDatabase):Array<String?>{
        var res: Array<String?> = arrayOfNulls<String>(9000)
        val cursor =db.query("Images", null, null, null, null, null, null)
        var cnt=0
        if (cursor.moveToFirst()) {
            do {
                cnt+=1
                // 遍历Cursor对象，取出数据并打印
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val url = cursor.getString(cursor.getColumnIndex("url"))
                var describe=""
                if(cursor.getColumnIndex("describe")!=-1)
                    describe= cursor.getString(cursor.getColumnIndex("describe"))
                res.set(id*4,id.toString())
                res.set(id*4+1,name)
                res.set(id*4+2,url)
                res.set(id*4+3,describe)
            } while (cursor.moveToNext())
        }
        res.set(0,cnt.toString())
        cursor.close()
        return res
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(dropImage)
        db.execSQL(createImage)
        Toast.makeText(context, "Create again succeeded", Toast.LENGTH_SHORT).show()
    }
}