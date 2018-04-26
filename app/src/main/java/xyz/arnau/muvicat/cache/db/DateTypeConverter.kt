package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }
}