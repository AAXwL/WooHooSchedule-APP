package com.woohoo.schedule


import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import android.content.Context

/**
 * Constructor: Context, database name, and cursor allow us to return a custom cursor position when querying data,
 * usually null, and the fourth parameter indicates the current library version number (used to update the database).
 */
class MyDatabaseHelper
    (private val mContext: Context) : SQLiteOpenHelper(mContext, "ListDatabase.db", null, 1) {
    //dbHelper = new MyDatabaseHelper(ListContent.this, "ListDatabase.db", null, 1);
    override fun onCreate(db: SQLiteDatabase) {
        //ExecSQL () in SQLiteDatabase is called to execute the table building sentence.
        db.execSQL(CREATE_List)
        db.execSQL(CREATE_Content)
        //Creating list successfully
        Toast.makeText(mContext, "Initializing the WooHoo Schedule list succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists List")
        db.execSQL("drop table if exists Content")
        onCreate(db)
    }

    companion object {
        const val CREATE_List =
            "create table List(" +  //Autoincrement indicates that the ID column is self-growing
                    "id integer primary key autoincrement," +
                    "listname text," +
                    "countAll integer," +
                    "countFinish integer," +
                    "deadline integer," +
                    "status integer)"
        const val CREATE_Content =
            "create table Content(" +  //Autoincrement indicates that the ID column is self-growing
                    "id integer primary key autoincrement," +
                    "isFinish integer," +
                    "content text," +
                    "status integer," +
                    "listid integer)"
    }
}