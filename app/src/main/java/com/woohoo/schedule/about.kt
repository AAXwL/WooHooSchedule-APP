package com.woohoo.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

class about : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    //Top left return key, same as other pages
    fun backviewonClick(view: View?) {
        finish()
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
    }

    //Rewrite phone back key, add switch animation, other pages the same
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out)
        }
        return super.onKeyUp(keyCode, event)
    }
}