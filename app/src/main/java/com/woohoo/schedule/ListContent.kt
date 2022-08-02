package com.woohoo.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import android.view.MotionEvent
import com.woohoo.schedule.CommonDialog.OnClickBottomListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListContent : AppCompatActivity() {
    private var db: MyDatabaseDAO? = null
    private var listId = 0
    private var listName: String? = null
    private var listView: ListView? = null
    private var adapter: ListViewAdapterContent? = null
    private var datalist: ArrayList<Map<String?, Any?>>? = null
    private var addcontent: EditText? = null
    private var title: TextView? = null
    private var info: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_content)
        //Parse the passed Listid
        listId = intent.getStringExtra("listid")!!.toInt()
        //Database initialization
        db = MyDatabaseDAO(this@ListContent)
        //Query the list name from the database with listid and set it to title. There is one or only one ID
        title = findViewById(R.id.listtitle)


        //Listen for recycle bin and archive buttons
        findViewById<View>(R.id.btn_MoveToTrash).setOnClickListener { view: View -> onClick(view) }
        findViewById<View>(R.id.btn_MoveToArchive).setOnClickListener { view: View -> onClick(view) }
        findViewById<View>(R.id.btn_updateList).setOnClickListener { view: View -> onClick(view) }

        //listview
        listView = findViewById(R.id.listview)
        datalist = data
        adapter = ListViewAdapterContent(this@ListContent, datalist!!)
        val addView = layoutInflater.inflate(R.layout.content_item_add, null)
        addcontent = addView.findViewById(R.id.addcontent)
        info = addView.findViewById(R.id.info)
        listView!!.addFooterView(addView, null, false)
        listView!!.setAdapter(adapter)

        //Listview click listen
        listView!!.setOnItemClickListener(OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val contentinfo = listView!!.getItemAtPosition(position) as HashMap<String, Any>
            val contentId = contentinfo["id"] as Int
            val contentStatus = contentinfo["status"] as Int
            db!!.updateContent(contentId, contentStatus, listId)
            refresh()
        })
        listView!!.setOnItemLongClickListener(OnItemLongClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val contentinfo = listView!!.getItemAtPosition(position) as HashMap<String, Any>
            val contentId = contentinfo["id"] as Int
            //Toast.makeText(ListContent.this, String.valueOf(contentid), Toast.LENGTH_SHORT).show();
            db!!.deleteContent(contentId, listId)
            refresh()
            true
        })
        addcontent!!.setOnEditorActionListener(OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val contentName = addcontent!!.getText().toString().trim { it <= ' ' }
                if (contentName.length != 0) {
                    db!!.addContent(contentName, listId)
                    refresh()
                    addcontent!!.setText("")
                    //listView!!.setSelection(listView!!.getBottom());
                } else {
                    hidekeyboard(v)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onStart() {
        super.onStart()
        var map: Map<String?, Any?> = HashMap()
        map = db!!.queryListInfo(listId)
        listName = map["listName"] as String?
        val ddl = map["ddl"] as Long?
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(ddl!! * 1000)
        val nowTime = Date(System.currentTimeMillis())
        val DDL = sdf.format(date) //ddl
        title!!.text = listName
        info!!.text = "Deadline：$DDL"
        val todayDate = sdf.format(nowTime)
        var todayTimeStamp: Long = 0
        try {
            todayTimeStamp = sdf.parse(todayDate).time / 1000 //Today's time stamp
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (ddl < todayTimeStamp) { //expired red
            info!!.setTextColor(Color.parseColor("#FF2D55"))
        } else { //Unexpired black
            info!!.setTextColor(Color.parseColor("#454545"))
        }
    }

    fun hidekeyboard(v: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        addcontent!!.clearFocus()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev, null)
        addcontent!!.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    val data: ArrayList<Map<String?, Any?>>
        get() = db!!.queryContent(listId)

    //回收站与归档按钮操作
    fun onClick(view: View) {
        val viewId = view.id
        if (viewId == R.id.btn_MoveToTrash) {
            val dialog = CommonDialog(this@ListContent)
            dialog.setTitle("Are you sure you want to delete $listName ？")
                .setPositive("Delete")
                .setPositiveColor(Color.parseColor("#ff2d55"))
                .setNegtive("Cancel")
                .setMessage("Will move to the recycle bin from which this list can be recovered.")
                .setOnClickBottomListener(object : OnClickBottomListener {
                    override fun onPositiveClick() {
                        dialog.dismiss()
                        db!!.updateList(listId, 1)
                        finish()
                        overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
                    }

                    override fun onNegtiveClick() {
                        dialog.dismiss()
                    }
                }).show()
        } else if (viewId == R.id.btn_MoveToArchive) {
            val dialog = CommonDialog(this@ListContent)
            dialog.setTitle("Are you sure that you want to achieve $listName ？")
                .setPositive("Achieve")
                .setPositiveColor(Color.parseColor("#ff9500"))
                .setNegtive("Cancel")
                .setMessage("Moves to archived, from which you can recover this listing.")
                .setOnClickBottomListener(object : OnClickBottomListener {
                    override fun onPositiveClick() {
                        dialog.dismiss()
                        db!!.updateList(listId, 2)
                        finish()
                        overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
                    }

                    override fun onNegtiveClick() {
                        dialog.dismiss()
                    }
                }).show()
        } else if (viewId == R.id.btn_updateList) {
            val intent = Intent()
            intent.setClass(this@ListContent, updatelist::class.java)
            intent.putExtra("listId", listId.toString())
            startActivity(intent)
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        } else {
            Toast.makeText(this@ListContent, "Critical error！", Toast.LENGTH_SHORT).show()
        }
    }

    //刷新listview
    fun refresh() {
        //refresh list
        datalist!!.clear()
        datalist!!.addAll(data)
        adapter!!.notifyDataSetChanged()
    }

    //back click
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