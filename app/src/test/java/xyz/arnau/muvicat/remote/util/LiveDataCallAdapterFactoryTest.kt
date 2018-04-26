package xyz.arnau.muvicat.remote.util

import android.arch.lifecycle.LiveData
import com.google.common.reflect.TypeToken
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit


@RunWith(JUnit4::class)
class LiveDataCallAdapterFactoryTest {

    @get:Rule
    private val server = MockWebServer()
    private val factory = LiveDataCallAdapterFactory()
    private var retrofit: Retrofit? = null

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(factory)
                .build()
    }

    @Test
    fun responseType() {
        val bodyClass = object : TypeToken<LiveData<ApiResponse<String>>>() {}.type
        assertEquals(object : TypeToken<String>() {}.type, factory.get(bodyClass, NO_ANNOTATIONS, retrofit!!)!!.responseType())

        val bodyWildcard = object : TypeToken<LiveData<ApiResponse<out String>>>() {}.type
        assertEquals(object : TypeToken<String>() {}.type, factory.get(bodyWildcard, NO_ANNOTATIONS, retrofit!!)!!.responseType())
    }

    @Test
    fun nonListenableFutureReturnsNull() {
        val adapter = factory.get(String::class.java, NO_ANNOTATIONS, retrofit!!)
        assertEquals(null, adapter)
    }

    @Test
    fun rawTypesThrows() {
        val liveDataType = object : TypeToken<LiveData<*>>() {}.type
        try {
            val callAdapter = factory.get(liveDataType, NO_ANNOTATIONS, retrofit!!)
            fail("Unexpected callAdapter = " + callAdapter!!.javaClass.name)
        } catch (e: IllegalArgumentException) {
            assertEquals("type must be a resource", e.message)
        }

    }

    companion object {
        private val NO_ANNOTATIONS = arrayOfNulls<Annotation>(0)
    }
}
