package com.woohoo.schedule

import android.view.LayoutInflater
import android.view.ViewGroup

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.*

class ListViewAdapterContent(
    private val context: Context,
    private val data: ArrayList<Map<String?, Any?>>
) : BaseAdapter() {
    private val layoutInflater: LayoutInflater

    /**
     *
     *A collection of components that correspond to controls in list.xml
     * @author Administrator
     */
    inner class Zujian {
        var image: ImageView? = null
        var title: TextView? = null
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
            convertView = layoutInflater.inflate(R.layout.contect_list_item, null)
            zujian.image = convertView.findViewById<View>(R.id.image) as ImageView
            zujian.title = convertView.findViewById<View>(R.id.title) as TextView
            convertView.tag = zujian
        } else {
            zujian = convertView.tag as Zujian
        }
        //Data binding

        zujian.image!!.setImageResource(data[position]["image"] as Int)
        zujian.title!!.text = data[position]["title"] as String?
        val status = data[position]["status"] as Int
        if (status == 1) {
            zujian.title!!.setTextColor(Color.parseColor("#c4c4c4"))
            zujian.title!!.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            zujian.title!!.paint.isAntiAlias = true
        } else if (status == 0) {
            zujian.title!!.setTextColor(Color.parseColor("#454545"))
            zujian.title!!.paint.flags = 0
            zujian.title!!.paint.isAntiAlias = true
        }
        return convertView!!
    }

    init {
        layoutInflater = LayoutInflater.from(context)
    }
}