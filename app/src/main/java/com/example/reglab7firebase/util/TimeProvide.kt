package com.example.reglab7firebase.util


import com.google.firebase.Timestamp
import java.util.Calendar


interface TimeProvider {
    fun getCurrentTimestamp(): Timestamp
    fun getCalendarInstance(): Calendar
}

class AndroidTimeProvider : TimeProvider {
    override fun getCurrentTimestamp(): Timestamp = Timestamp.now()
    override fun getCalendarInstance(): Calendar = Calendar.getInstance()
}