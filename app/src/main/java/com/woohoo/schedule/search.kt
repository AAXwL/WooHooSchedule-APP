package com.woohoo.schedule

import com.woohoo.schedule.DisplayUtils.hideInputWhenTouchOtherView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woohoo.schedule.R
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import android.content.Intent
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.ArrayList
import java.util.HashMap

class search : AppCompatActivity() {
    //Global variable declarations are not initialized
    private var db //databaseDao
            : MyDatabaseDAO? = null
    private var listView //lv
            : ListView? = null
    private var adapter //Lv is configured
            : ListViewAdapter? = null
    private var datalist //The Datalist data set, which is the LV content
            : ArrayList<Map<String?, Any?>>? = null
    private var searchList: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //Override the oncreate
        setContentView(R.layout.activity_search) //settting layout
        db = MyDatabaseDAO(this@search) //Create a database DAO entity
        listView = findViewById(R.id.resultList) //listview
        datalist = ArrayList() //Initialize datalist to an empty list
        adapter = ListViewAdapter(this@search, datalist!!) //Initialize the Adapter of the LV. Datalist is null
        val search_foot = layoutInflater.inflate(R.layout.search_foot, null)
        listView!!.addFooterView(search_foot, null, false)
        listView!!.adapter = adapter //Set the Adapter of the LV
        searchList = findViewById(R.id.search_text)
        searchList!!.isFocusable = true
        searchList!!.isFocusableInTouchMode = true
        searchList!!.requestFocus()
        searchList!!.setOnEditorActionListener(OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->  //Set the keyboard search key and carriage return listening
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) { //If there is a search/enter order
                val listName = searchList!!.getText().toString().trim { it <= ' ' } //Get the text in ET
                datalist!!.clear() //datalist clear
                if (listName.isNotEmpty()) {
                    datalist!!.addAll(getData(listName)) //Find from the database and add the results to the datalist
                }
                adapter!!.notifyDataSetChanged() //refresh listview
                hidekeyboard(v)
                return@OnEditorActionListener true //finished
            }
            false //finished
        })
        listView!!.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val listinfo = listView!!.getItemAtPosition(position) as HashMap<String, Any>
            Log.d("aaa", listinfo.toString())
            val listId = listinfo["id"].toString()
            val intent = Intent()
            intent.setClass(this@search, ListContent::class.java)
            intent.putExtra("listid", listId)
            startActivity(intent)
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
    }

    fun getData(name: String): List<Map<String?, Any?>> {
        return db!!.queryList(1, 0, name)
    } //search from db

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

    fun hidekeyboard(v: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        hideInputWhenTouchOtherView(this, ev, null)
        Log.d("TAG", "dispatchTouchEvent: 1")
        return super.dispatchTouchEvent(ev)
    }
}