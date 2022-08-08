package com.woohoo.schedule

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel()
{
    val user_image = MutableLiveData<Bitmap>()
}