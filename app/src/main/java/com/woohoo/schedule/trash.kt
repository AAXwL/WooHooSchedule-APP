package com.woohoo.schedule

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ListView
import com.woohoo.schedule.CommonDialog.OnClickBottomListener
import java.util.HashMap

class trash : AppCompatActivity(), View.OnClickListener {
    private var listView: ListView? = null
    private var db: MyDatabaseDAO? = null
    private var adapter: ListViewAdapterTrash? = null
    private var datalist: ArrayList<Map<String?, Any?>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        db = MyDatabaseDAO(this@trash)
        listView = findViewById(R.id.list_view_trash)
        val emptyView = findViewById<View>(R.id.empty)
        listView!!.setEmptyView(emptyView)
        datalist = data
        adapter = ListViewAdapterTrash(this@trash, datalist!!)
        listView!!.setAdapter(adapter)
    }

    //获取数据库中回收站数据
    val data: java.util.ArrayList<Map<String?, Any?>>
        get() = db!!.queryList(0, 1, null)

    //清空回收站
    fun ClearTrash(view: View?) {
        val dialog = CommonDialog(this@trash)
        dialog.setTitle("Are you sure you want to empty the recycle bin？")
            .setPositive("Yes").setPositiveColor(Color.parseColor("#ff2d55"))
            .setNegtive("No")
            .setMessage("The recycle bin will be emptied immediately and cannot be undone.")
            .setOnClickBottomListener(object : OnClickBottomListener {
                override fun onPositiveClick() {
                    dialog.dismiss()
                    db!!.clearTrash()
                    finish()
                    overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
                }

                override fun onNegtiveClick() {
                    dialog.dismiss()
                }
            }).show()
    }

    //click on event
    override fun onClick(v: View) {
        val btn_id = v.id
        val listinfo =
            listView!!.getItemAtPosition(v.tag as Int) as HashMap<*, *> //SimpleAdapter返回Map
        val listId = listinfo["id"] as Int
        val listName = listinfo["title"].toString()
        //clickOnDelete
        if (btn_id == R.id.btn_trash_archive_delete) {
            val dialog = CommonDialog(this@trash)
            dialog.setTitle("Are you sure you want to delete $listName ？")
                .setPositive("Yes").setPositiveColor(Color.parseColor("#ff2d55"))
                .setNegtive("No")
                .setMessage("This list is cleared immediately and cannot be undone.")
                .setOnClickBottomListener(object : OnClickBottomListener {
                    override fun onPositiveClick() {
                        dialog.dismiss()
                        db!!.deleteList(listId)
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

    fun refresh() {
        //刷新list
        datalist!!.clear()
        datalist!!.addAll(data)
        adapter!!.notifyDataSetChanged()
    }

    //返回键
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