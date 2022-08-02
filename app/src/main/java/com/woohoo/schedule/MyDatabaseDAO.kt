package com.woohoo.schedule


import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyDatabaseDAO(context: Context) {
    private val dbHelper: MyDatabaseHelper
    private val mcontext: Context
    fun addList(listName: String?, listDate: Long): Long {
        val db = dbHelper.writableDatabase //Getting the database
        val values = ContentValues()
        values.put("listname", listName)
        values.put("countAll", 0)
        values.put("countFinish", 0)
        values.put("status", 0)
        values.put("deadline", listDate)
        val rowid = db.insert("List", null, values)
        values.clear()
        return rowid
    }

    fun clearTrash() {
        val db = dbHelper.writableDatabase //Getting the database
        db.delete("List", "status = 1", null)
        db.delete("Content", "status = 1", null)
        db.close()
    }

    fun deleteList(listId: Int) {
        val db = dbHelper.writableDatabase //Getting the database
        db.delete("List", "id=$listId", null)
        db.delete("Content", "listid=$listId", null)
    }

    fun updateList(listId: Int, status: Int) {
        val db = dbHelper.writableDatabase //Getting the database
        val values = ContentValues()
        values.put("status", status)
        db.update("List", values, "id=$listId", null)
        db.update("Content", values, "listid=$listId", null)
        values.clear()
    }

    fun updateListNamedate(listId: Int, listName: String?, listDate: Long?) {
        val db = dbHelper.writableDatabase //Getting the database
        val values = ContentValues()
        values.put("listname", listName)
        values.put("deadline", listDate)
        db.update("List", values, "id=$listId", null)
        values.clear()
    }

    @SuppressLint("SimpleDateFormat", "Range")
    fun queryList(method: Int, status: Int, listname: String?): ArrayList<Map<String?, Any?>> {
        //method 0 = id,1 = name
        val db = dbHelper.writableDatabase //Getting the database
        val result: ArrayList<Map<String?, Any?>> = ArrayList()
        val listList: Cursor
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("Recycle") val load_icon =
            mcontext.resources.obtainTypedArray(R.array.load_icon)
        listList = if (method == 0) {
            db.query("List", null, "status=$status", null, null, null, "deadline")
        } else {
            db.rawQuery("select * from List where status = 0 and listname like '%$listname%'", null)
        }
        if (listList.moveToFirst()) {
            do {
                //It then retrieves the index of the corresponding position in a column by using the getColumnIndex() of the Cursor
                var ExtraString: String
                var ddlstatus = 0
                var isFinish = 0
                val map: MutableMap<String?, Any?> = HashMap()
                val name = listList.getString(listList.getColumnIndex("listname"))
                val listid = listList.getInt(listList.getColumnIndex("id"))
                map["id"] = listid
                map["title"] = name
                val countAll = listList.getInt(listList.getColumnIndex("countAll"))
                val countFinish = listList.getInt(listList.getColumnIndex("countFinish"))
                var load = 0
                if (countAll != 0) {
                    load = countFinish * 100 / countAll
                    if (load == 100) {
                        isFinish = 1
                    }
                }
                map["image"] = load_icon.getResourceId(load / 10, 0)
                //Time calculate
                if (isFinish != 1) {
                    val ddl = listList.getLong(listList.getColumnIndex("deadline")) //Cut-off time stamp
                    val nowTime = Date(System.currentTimeMillis())
                    val todayDate = sdf.format(nowTime)
                    var todayTimeStamp: Long = 0
                    try {
                        todayTimeStamp = sdf.parse(todayDate).time / 1000 //Today's time stamp

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    if (ddl > todayTimeStamp) { //Return the remaining days without expiration
                        val diff = ddl - todayTimeStamp
                        val days = diff / (60 * 60 * 24)
                        if (days > 1)
                        {
                            ExtraString = "and $days days left"
                        }
                        else
                        {
                            ExtraString = "and $days day left"
                        }
                    } else if (ddl == todayTimeStamp) {
                        ExtraString = "Today"
                    } else { //Expired return a different picture, not yet
                        val diff = todayTimeStamp - ddl
                        val days = diff / (60 * 60 * 24)
                        ExtraString = "and has expired $days days"
                        ddlstatus = 1
                    }
                    //End of the time
                } else {
                    ExtraString = "and Done"
                }
                map["ddl"] = ddlstatus
                map["isFinish"] = isFinish
                map["info"] =
                    countFinish.toString() + " Completed  " + (countAll - countFinish) + " Todo " + ExtraString
                result.add(map)
            } while (listList.moveToNext())
        }
        listList.close()
        db.close()
        //The sorting
        if (!result.isEmpty()) {
            Collections.sort(result) { object1: Map<String?, Any?>, object2: Map<String?, Any?> -> object1["isFinish"] as Int - object2["isFinish"] as Int }
        }
        return result
    }

    @SuppressLint("Range")
    fun queryListInfo(listId: Int): Map<String?, Any?> {
        val db = dbHelper.writableDatabase //Getting the database
        val map: MutableMap<String?, Any?> = HashMap()
        val listname = db.query("List", null, "id=$listId", null, null, null, null)
        listname.moveToFirst()
        val listName = listname.getString(listname.getColumnIndex("listname"))
        val ddl = listname.getLong(listname.getColumnIndex("deadline")) //Deadline time stamp
        //Log.d("listname",listName);
        map["listName"] = listName
        map["ddl"] = ddl
        listname.close()
        return map
    }

    @SuppressLint("Range")
    fun queryContent(listId: Int): ArrayList<Map<String?, Any?>> {
        val db = dbHelper.writableDatabase //Getting the database
        val result: ArrayList<Map<String?, Any?>> = ArrayList()
        @SuppressLint("Recycle") val checked_icon =
            mcontext.resources.obtainTypedArray(R.array.checked_icon)
        val listContent = db.query("Content", null, "listid=$listId", null, null, null, null)
        if (listContent.moveToFirst()) {
            do {
                val contentName = listContent.getString(listContent.getColumnIndex("content"))
                val id = listContent.getInt(listContent.getColumnIndex("id"))
                val status = listContent.getInt(listContent.getColumnIndex("isFinish"))
                val map: MutableMap<String?, Any?> = HashMap()
                map["id"] = id
                map["title"] = contentName
                map["image"] = checked_icon.getResourceId(status, 0)
                map["status"] = status
                result.add(map)
            } while (listContent.moveToNext())
        }
        listContent.close()
        db.close()
        return result
    }

    fun addContent(contentName: String?, listId: Int) {
        val db = dbHelper.writableDatabase //Getting the database
        val values = ContentValues()
        values.put("content", contentName)
        values.put("listid", listId)
        values.put("isFinish", 0)
        values.put("status", 0)
        db.insert("Content", null, values)
        values.clear()
        syncdblite(db, listId)
    }

    fun deleteContent(contentId: Int, listId: Int) {
        val db = dbHelper.writableDatabase //Getting the database
        db.delete("Content", "id=$contentId", null)
        syncdblite(db, listId)
    }

    fun updateContent(contentId: Int, contentStatus: Int, listId: Int) {
        val db = dbHelper.writableDatabase //Getting the database
        val values = ContentValues()
        values.put("isFinish", 1 xor contentStatus)
        db.update("Content", values, "id=$contentId", null)
        syncdblite(db, listId)
    }

    private fun syncdblite(db: SQLiteDatabase, listId: Int) {
        val countALL = db.query("Content", null, "listid=$listId", null, null, null, null)
        val countAll = countALL.count
        countALL.close()
        val countFINISH =
            db.query("Content", null, "listid=$listId AND isFinish=1", null, null, null, null)
        val countFinish = countFINISH.count
        countFINISH.close()
        val values_sync = ContentValues()
        values_sync.put("countFinish", countFinish)
        values_sync.put("countAll", countAll)
        db.update("List", values_sync, "id=$listId", null)
        values_sync.clear()
    }

    init {
        dbHelper = MyDatabaseHelper(context)
        mcontext = context
    }
}