package com.woohoo.schedule

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object DisplayUtils {
    @JvmStatic
    fun hideInputWhenTouchOtherView(
        activity: Activity,
        ev: MotionEvent,
        excludeViews: List<View?>?
    ) {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (excludeViews != null && excludeViews.isNotEmpty()) {
                for (i in excludeViews.indices) {
                    if (isTouchView(excludeViews[i], ev)) {
                        return
                    }
                }
            }
            val v = activity.currentFocus
            if (isShouldHideInput(v, ev)) {
                val inputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
        }
    }

    fun isTouchView(view: View?, event: MotionEvent?): Boolean {
        if (view == null || event == null) {
            return false
        }
        val leftTop = intArrayOf(0, 0)
        view.getLocationInWindow(leftTop)
        val left = leftTop[0]
        val top = leftTop[1]
        val bottom = top + view.height
        val right = left + view.width
        return event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom
    }

    fun isShouldHideInput(v: View?, event: MotionEvent?): Boolean {
        return if (v != null && v is EditText) {
            !isTouchView(v, event)
        } else false
    }
}