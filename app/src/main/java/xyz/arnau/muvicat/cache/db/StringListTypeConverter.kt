package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class StringListTypeConverter {
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null)
            return null

        val list = value.split(',')
        return list.dropLast(1)
    }

    @TypeConverter
    fun toString(value: List<String>?): String? {
        if (value == null)
            return null

        var string = ""
        value.forEach { string += "$it," }
        return string
    }
}