package com.woohoo.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import android.app.DatePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.view.*
import android.widget.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class updatelist : AppCompatActivity() {
    private var listId = 0
    private var db: MyDatabaseDAO? = null
    private var listName: String? = null
    private var listNameInput: EditText? = null
    private var listDatePicker: TextView? = null
    private var calendar: Calendar? = null
    private var date: Date? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)
        listId = intent.getStringExtra("listId")!!.toInt()
        db = MyDatabaseDAO(this@updatelist)
        val titleText = findViewById<TextView>(R.id.title)
        val icon = findViewById<ImageView>(R.id.imageView2)
        titleText.setText(R.string.update_list)
        icon.setImageResource(R.drawable.ic_updatelogo)
        listNameInput = findViewById(R.id.listName)
        listDatePicker = findViewById(R.id.listTime)
        var map: Map<String?, Any?>? = HashMap()
        map = db!!.queryListInfo(listId)
        listName = map["listName"] as String?
        val ddl = map["ddl"] as Long?
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        date = Date(ddl!! * 1000)
        calendar = Calendar.getInstance()
        calendar!!.setTime(date)
        val DDL = sdf.format(date) //ddl
        listNameInput!!.setText(listName)
        listDatePicker!!.setText(DDL)
        listNameInput!!.setFocusable(true)
        listNameInput!!.setFocusableInTouchMode(true)
        listNameInput!!.requestFocus()
        listNameInput!!.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                listName = listNameInput!!.getText().toString().trim { it <= ' ' }
                //Make sure the list name is not empty
                if (listName!!.length != 0) {
                    showDatePickerDialog(this@updatelist, 0)
                    //select date
                } else {
                    Toast.makeText(this@updatelist, "Please enter the title", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })

        listDatePicker!!.setOnClickListener(View.OnClickListener {
            listName = listNameInput!!.getText().toString().trim { it <= ' ' }
            //确保list名字不为空
            if (listName!!.length != 0) {
                showDatePickerDialog(this@updatelist, 0)
                //选择日期
            } else {
                Toast.makeText(this@updatelist, "Please enter the title first", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showDatePickerDialog(context: Context?, themeResId: Int) {
        DatePickerDialog(
            context!!,
            themeResId,
            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                calendar!![year, month] = dayOfMonth
                var timestamp: Long = 0
                val listDate = sdf.format(calendar!!.time)
                try {
                    timestamp = sdf.parse(listDate).time / 1000
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                listDatePicker!!.text = listDate
                //数据库操作
                db!!.updateListNamedate(listId, listName, timestamp)
                makesound()
                finish()
                overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
            },
            calendar!![Calendar.YEAR],
            calendar!![Calendar.MONTH],
            calendar!![Calendar.DAY_OF_MONTH]
        ).show()
    }

    //back
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

    private fun makesound()
    {
        object: Thread()
        {
            override fun run()
            {
                super.run()
                val mediaPlayer = MediaPlayer.create(this@updatelist, R.raw.confirmed)
                sleep(500)
                var isStopThread = false
                while (true)
                {
                    if (isStopThread)
                    {
                        mediaPlayer?.stop()
                        break
                    }
                    mediaPlayer?.start()
                    sleep(1200)
                    isStopThread = true
                }
            }
        }.start()
    }
}