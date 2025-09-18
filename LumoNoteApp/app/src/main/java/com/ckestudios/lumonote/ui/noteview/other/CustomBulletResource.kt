package com.ckestudios.lumonote.ui.noteview.other

import android.util.Log
import androidx.lifecycle.MutableLiveData

object CustomBulletResource {

    val customBullet = MutableLiveData<String>()


    fun setCustomBullet(bullet: String) {

        customBullet.value = bullet
        Log.d("textformatfrag", "bullet: $bullet")
    }
}
