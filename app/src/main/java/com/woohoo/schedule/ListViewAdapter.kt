package com.woohoo.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import android.content.Context
import android.view.View
import android.widget.*
import java.util.ArrayList

class ListViewAdapter(private val context: Context, private val data: ArrayList<Map<String?, Any?>>) :
    BaseAdapter() {
    private val layoutInflater: LayoutInflater

    //A collection of components that correspond to controls in list.xml
    inner class Zujian {
        var image: ImageView? = null
        var image2: ImageView? = null
        var title: TextView? = null
        var view: Button? = null
        var info: TextView? = null
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
            convertView = layoutInflater.inflate(R.layout.home_list_item, null)
            zujian.image = convertView.findViewById(R.id.image)
            zujian.image2 = convertView.findViewById(R.id.image2)
            zujian.title = convertView.findViewById(R.id.title)
            zujian.info = convertView.findViewById(R.id.info)
            convertView.tag = zujian
        } else {
            zujian = convertView.tag as Zujian
        }
        //Data binding
        zujian.image!!.setImageResource(data[position]["image"] as Int)
        //Picture  Settings
        val ddlstatus = data[position]["ddl"] as Int
        val isFinish = data[position]["isFinish"] as Int
        if (ddlstatus == 1 && isFinish == 0) {
            zujian.image!!.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.trash))
            //Set !
            zujian.image2!!.setImageResource(R.drawable.ic_load_err)
        } else if (isFinish == 1) {
            zujian.image!!.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            zujian.image2!!.setImageDrawable(null)
        } else {
            zujian.image!!.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.classicalBlue))
            zujian.image2!!.setImageDrawable(null)
        }
        zujian.title!!.text = data[position]["title"] as String?
        zujian.info!!.text = data[position]["info"] as String?
        return convertView!!
    }

    init {
        layoutInflater = LayoutInflater.from(context)
    }
}