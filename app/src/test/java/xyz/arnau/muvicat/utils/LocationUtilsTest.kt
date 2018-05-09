package xyz.arnau.muvicat.utils

import android.location.Location
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class LocationUtilsTest {
    @Test
    fun getDistanceReturnsCorrectDistanceBetweenCoordinates() {
        val location = mock(Location::class.java)
        `when`(location.latitude).thenReturn(59.3498195)
        `when`(location.longitude).thenReturn(18.0705110)
        val latitude =  41.3894841
        val longitude =  2.1133859

        val distance = LocationUtils.getDistance(location, latitude, longitude)

        assertEquals(2282, distance)
    }
}