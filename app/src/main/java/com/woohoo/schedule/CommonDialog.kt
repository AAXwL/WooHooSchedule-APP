package com.woohoo.schedule

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import android.os.Bundle
import android.text.TextUtils
import android.view.View

/**
 * description: custom dialog
 */
class CommonDialog  //
    (context: Context?) : Dialog(context!!, R.style.CustomDialog) {
    /**
     * Display title
     */
    private var titleTv: TextView? = null

    /**
     * Displayed messages
     */
    private var messageTv: TextView? = null

    /**
     * Confirm and cancel buttons
     */
    private var negtiveBn: TextView? = null
    private var positiveBn: TextView? = null

    /**
     * It's content data
     */
    var message: String? = null
        private set
    var title: String? = null
        private set
    var positive: String? = null
        private set
    var negtive: String? = null
        private set
    private var color = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete)
        //You cannot cancel the animation by clicking on the blank space
        setCanceledOnTouchOutside(false)
        //Initialize the interface control
        initView()
        //Initialize interface data
        refreshView()
        //Event that initializes an interface control
        initEvent()
    }

    private fun initEvent() {
        positiveBn!!.setOnClickListener {
            if (onClickBottomListener != null) {
                onClickBottomListener!!.onPositiveClick()
            }
        }
        negtiveBn!!.setOnClickListener {
            if (onClickBottomListener != null) {
                onClickBottomListener!!.onNegtiveClick()
            }
        }
    }

    private fun refreshView() {
        if (!TextUtils.isEmpty(title)) {
            titleTv!!.text = title
            titleTv!!.visibility = View.VISIBLE
        } else {
            titleTv!!.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(message)) {
            messageTv!!.text = message
            messageTv!!.visibility = View.VISIBLE
        } else {
            messageTv!!.visibility = View.GONE
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty(positive)) {
            positiveBn!!.text = positive
        } else {
            positiveBn!!.text = "确定"
        }
        if (!TextUtils.isEmpty(negtive)) {
            negtiveBn!!.text = negtive
        } else {
            negtiveBn!!.text = "取消"
        }
        //颜色
        positiveBn!!.setTextColor(color)
    }

    override fun show() {
        super.show()
        refreshView()
    }

    private fun initView() {
        negtiveBn = findViewById<View>(R.id.dismiss) as TextView
        positiveBn = findViewById<View>(R.id.confirm) as TextView
        titleTv = findViewById<View>(R.id.title) as TextView
        messageTv = findViewById<View>(R.id.message) as TextView
    }

    var onClickBottomListener: OnClickBottomListener? = null
    fun setOnClickBottomListener(onClickBottomListener: OnClickBottomListener?): CommonDialog {
        this.onClickBottomListener = onClickBottomListener
        return this
    }

    interface OnClickBottomListener {

        fun onPositiveClick()

        fun onNegtiveClick()
    }

    fun setMessage(message: String?): CommonDialog {
        this.message = message
        return this
    }

    fun setTitle(title: String?): CommonDialog {
        this.title = title
        return this
    }

    fun setPositive(positive: String?): CommonDialog {
        this.positive = positive
        return this
    }

    fun setPositiveColor(color: Int): CommonDialog {
        this.color = color
        return this
    }

    fun setNegtive(negtive: String?): CommonDialog {
        this.negtive = negtive
        return this
    }
}