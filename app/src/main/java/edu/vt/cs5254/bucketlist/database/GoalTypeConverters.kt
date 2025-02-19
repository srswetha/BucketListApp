package edu.vt.cs5254.bucketlist.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class GoalTypeConverters {


    @TypeConverter
    fun longToDate(millis: Long?): Date? = millis?.let { Date(it) }

    @TypeConverter
    fun dateToLong(date: Date?): Long? = date?.time
}
