package com.mohamed.abdo.myhealth.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Time {
    // to get the time with message
    fun timeStamp() : String{
        val timeStamp = Timestamp(System.currentTimeMillis())
        val format = SimpleDateFormat("HH:mm")
        val time = format.format(Date(timeStamp.time))
        return  time.toString()
    }
}