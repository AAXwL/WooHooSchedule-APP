package com.woohoo.schedule


import android.view.LayoutInflater
import android.view.ViewGroup

import android.view.View
import android.widget.*
import java.util.ArrayList

class ListViewAdapterArchive(
    private val listener: View.OnClickListener,
    private val data: ArrayList<Map<String?, Any?>>
) : BaseAdapter() {
    //A collection of components that correspond to controls in list.xml

    inner class Zujian {
        var title: TextView? = null
        var info: TextView? = null
        var btn_archive_delete: ImageView? = null
        var btn_archive_recover: ImageView? = null
    }

    override fun getCount(): Int {
        return data.size
    }

    /**
     * Get data for a location
     */
    override fun getItem(position: Int): Any {
        return data[position]
    }

    /**
     * Get unique identity
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val zujian: Zujian
        if (convertView == null) {
            zujian = Zujian()
            //Get the component, instantiate the component
            convertView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.trash_archive_list_item, parent, false)
            zujian.title = convertView.findViewById(R.id.title)
            zujian.info = convertView.findViewById(R.id.info)
            zujian.btn_archive_delete = convertView.findViewById(R.id.btn_trash_archive_delete)
            zujian.btn_archive_recover = convertView.findViewById(R.id.btn_trash_archive_recover)
            convertView.tag = zujian
        } else {
            zujian = convertView.tag as Zujian
        }
        //Data binding
        zujian.title!!.text = data[position]["title"] as String?
        zujian.info!!.text = data[position]["info"] as String?

        //Listening
        zujian.btn_archive_delete!!.setOnClickListener(listener)
        zujian.btn_archive_recover!!.setOnClickListener(listener)
        zujian.btn_archive_delete!!.tag = position
        zujian.btn_archive_recover!!.tag = position
        return convertView!!
    }
}