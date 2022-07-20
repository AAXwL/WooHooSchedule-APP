package com.woohoo.schedule

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddListActivity : AppCompatActivity() {
    private var listNameInput: EditText? = null
    private var listDatePicker: TextView? = null
    private var db: MyDatabaseDAO? = null
    private var calendar: Calendar? = null
    private var listName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)
        db = MyDatabaseDAO(this@AddListActivity)
        calendar = Calendar.getInstance()
        listDatePicker = findViewById(R.id.listTime)
        listNameInput = findViewById(R.id.listName)
        listNameInput!!.isFocusable = true
        listNameInput!!.isFocusableInTouchMode = true
        listNameInput!!.requestFocus()
        listNameInput!!.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                listName = listNameInput!!.text.toString().trim { it <= ' ' }
                //Edge case
                if (listName!!.isNotEmpty()) {
                    showDatePickerDialog(this@AddListActivity, 0)
                    //Select date
                } else {
                    Toast.makeText(this@AddListActivity, "Please enter a title", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })
        listDatePicker!!.setOnClickListener {
            listName = listNameInput!!.getText().toString().trim { it <= ' ' }
            //Edge case
            if (listName!!.isNotEmpty()) {
                showDatePickerDialog(this@AddListActivity, 0)
                //Select date
            } else {
                Toast.makeText(this@AddListActivity, "Please enter a title first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerDialog(context: Context?, themeResId: Int) {
        DatePickerDialog(
            context!!,
            themeResId,
            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                calendar!![year, month] = dayOfMonth
                var timestamp: Long = 0
                val listDate = sdf.format(calendar!!.time)
                try {
                    timestamp = sdf.parse(listDate)!!.time / 1000
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                listDatePicker!!.text = listDate
                val rowid = db!!.addList(listName, timestamp)
                val intent = Intent()
                intent.setClass(
                    this@AddListActivity,
                    ListContent::class.java
                ) //This precedes the current Activty name and class precedes the activity name to jump to
                intent.putExtra("listid", rowid.toString())
                startActivity(intent)
                overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
                finish()
            },
            calendar!![Calendar.YEAR],
            calendar!![Calendar.MONTH],
            calendar!![Calendar.DAY_OF_MONTH]
        ).show()
    }

    fun backviewonClick(view: View?) {
        finish()
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
        }
        return super.onKeyUp(keyCode, event)
    }
}