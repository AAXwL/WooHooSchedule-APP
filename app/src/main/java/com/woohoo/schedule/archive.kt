package com.woohoo.schedule

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ListView
import com.woohoo.schedule.R
import com.woohoo.schedule.CommonDialog.OnClickBottomListener
import java.util.ArrayList
import java.util.HashMap

class archive : AppCompatActivity(), View.OnClickListener {
    private var listView: ListView? = null
    private var db: MyDatabaseDAO? = null
    private var adapter: ListViewAdapterArchive? = null
    private var datalist: ArrayList<Map<String?, Any?>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        db = MyDatabaseDAO(this@archive)
        listView = findViewById(R.id.list_view_archive)
        val emptyView = findViewById<View>(R.id.empty)
        listView!!.emptyView = emptyView
        datalist = data
        adapter = ListViewAdapterArchive(this@archive, datalist!!)
        listView!!.setAdapter(adapter)
    }

    val data: ArrayList<Map<String?, Any?>>
        get() = db!!.queryList(0, 2, null)

    //OnClick
    override fun onClick(v: View) {
        val btn_id = v.id
        val listinfo =
            listView!!.getItemAtPosition(v.tag as Int) as HashMap<*, *> //SimpleAdapter返回Map
        val listId = listinfo["id"] as Int
        val listName = listinfo["title"].toString()
        //点击删除
        if (btn_id == R.id.btn_trash_archive_delete) {
            val dialog = CommonDialog(this@archive)
            dialog.setTitle("Are you sure you want to delete $listName ？")
                .setPositive("Delete")
                .setPositiveColor(Color.parseColor("#ff2d55"))
                .setNegtive("Cancel")
                .setMessage("Will move to the recycle bin from which this list can be recovered.")
                .setOnClickBottomListener(object : OnClickBottomListener {
                    override fun onPositiveClick() {
                        dialog.dismiss()
                        db!!.updateList(listId, 1)
                        refresh()
                    }

                    override fun onNegtiveClick() {
                        dialog.dismiss()
                    }
                }).show()
        } else if (btn_id == R.id.btn_trash_archive_recover) {
            db!!.updateList(listId, 0)
            refresh()
        }
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

    fun refresh() {
        //刷新list
        datalist!!.clear()
        datalist!!.addAll(data)
        adapter!!.notifyDataSetChanged()
    }
}