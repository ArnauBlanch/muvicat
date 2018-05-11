package xyz.arnau.muvicat.cache.utils

import xyz.arnau.muvicat.cache.model.PostalCodeEntity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class PostalCodeCsvReader {
    companion object {
        private const val CODE = 0
        private const val LATITUDE = 1
        private const val LONGITUDE = 2
    }

    fun readPostalCodeCsv(stream: InputStream): List<PostalCodeEntity> {
        val postalCodes = mutableListOf<PostalCodeEntity>()

        val fileReader = BufferedReader(InputStreamReader(stream))

        // ignoring header
        fileReader.readLine()

        var line = fileReader.readLine()
        while (line != null) {
            val tokens = line.split(',')
            if (tokens.size == 3) {
                val postalCode =
                    PostalCodeEntity(
                        tokens[CODE].toInt(),
                        tokens[LATITUDE].toDouble(),
                        tokens[LONGITUDE].toDouble()
                    )
                postalCodes += postalCode
            }

            line = fileReader.readLine()
        }
        return postalCodes
    }
}