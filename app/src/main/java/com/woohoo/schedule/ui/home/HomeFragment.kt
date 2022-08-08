package com.woohoo.schedule.ui.home

import com.woohoo.schedule.MyDatabaseDAO
import com.woohoo.schedule.ListViewAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.woohoo.schedule.R
import android.widget.AdapterView
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import com.woohoo.schedule.ListContent
import android.widget.ImageView
import android.widget.ListView
import com.woohoo.schedule.CommonDialog
import com.woohoo.schedule.CommonDialog.OnClickBottomListener
import com.woohoo.schedule.search
import androidx.fragment.app.Fragment
import java.util.ArrayList
import java.util.HashMap

class HomeFragment : Fragment()
{
    private var listView: ListView? = null
    private var db: MyDatabaseDAO? = null
    private var adapter: ListViewAdapter? = null
    private var datalist: ArrayList<Map<String?, Any?>>? = null

    //Get data from the database
    val data: ArrayList<Map<String?, Any?>>
        get() = db!!.queryList(0, 0, null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        listView = root.findViewById(R.id.list_view)
        db = MyDatabaseDAO(requireActivity())

        val emptyView = root.findViewById<View>(R.id.empty)
        listView!!.setEmptyView(emptyView)
        datalist = data
        adapter = ListViewAdapter(requireActivity(), datalist!!)
        listView!!.adapter = adapter
        listView!!.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val listinfo =
                listView!!.getItemAtPosition(position) as HashMap<String, Any> //SimpleAdapter return map
            Log.d("onClick", listinfo.toString())
            val listid = listinfo["id"].toString()
            val intent = Intent()
            intent.setClass(
                requireActivity(),
                ListContent::class.java
            ) //This precedes the current Activty name and class precedes the activity name to jump to
            intent.putExtra("listid", listid)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
        listView!!.setOnItemLongClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val listinfo =
                listView!!.getItemAtPosition(position) as HashMap<String, Any> //SimpleAdapter return Map
            val listId = listinfo["id"] as Int
            val name = listinfo["title"] as String?
            val dialog = CommonDialog(activity)
            dialog.setTitle("Are you sure you want to delete $name ？")
                .setPositive("Delete").setPositiveColor(Color.parseColor("#ff2d55"))
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
            true
        }
        val bt_search = root.findViewById<ImageView>(R.id.btn_search)
        bt_search.setOnClickListener { v: View? ->
            val intent = Intent()
            intent.setClass(requireActivity(), search::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
        return root
    }

    override fun onStart()
    {
        super.onStart()
        refresh()
    }

    //refresh listview
    fun refresh()
    {
        //refresh list
        datalist!!.clear()
        datalist!!.addAll(data)
        adapter!!.notifyDataSetChanged()
    }
}