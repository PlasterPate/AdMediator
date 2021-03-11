package com.example.admediator

import android.content.Context
import android.widget.Toast

class Test {

    fun toast(c: Context, s: String){
        Toast.makeText(c, s, Toast.LENGTH_LONG).show()
    }
}