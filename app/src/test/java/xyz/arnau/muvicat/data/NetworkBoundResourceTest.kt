/*package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.utils.CountingAppExecutors
import xyz.arnau.muvicat.utils.InstantAppExecutors
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

@RunWith(Parameterized::class)
class NetworkBoundResourceTest(private var useRealExecutors: Boolean) {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var saveResponse: Function<Response<String>, Void>
    private lateinit var shouldFetch: Function<Void, Boolean>
    private lateinit var createCall: Function<Void, LiveData<Response<String>>>
    private val dbData = MutableLiveData<String>()
    private lateinit var networkBoundResource: NetworkBoundResource<String>
    private val fetchedOnce = AtomicBoolean(false)
    private lateinit var countingAppExecutors: CountingAppExecutors

    companion object {
        @Parameterized.Parameters
        fun param(): List<Boolean> {
            return Arrays.asList(true, false)
        }
    }

    init {
        if (useRealExecutors) {
            countingAppExecutors = CountingAppExecutors()
        }
    }

    @Before
    fun setUp() {
        val appExecutors: AppExecutors =
                if (useRealExecutors)
                    countingAppExecutors.appExecutors
                else
                    InstantAppExecutors()
        networkBoundResource = object: NetworkBoundResource<String>(appExecutors) {
            override fun createCall(): LiveData<Response<String>> = createCall()

            override fun loadFromDb(): LiveData<String> = dbData

            override fun shouldFetch(): Boolean =
                    shouldFetch() && fetchedOnce.compareAndSet(false, true)

            override fun saveResponse(item: Response<String>) {
                saveResponse.apply(item)
            }

        }
    }

    private fun drain() {
        if (!useRealExecutors) {
            return
        }
        try {
            countingAppExecutors.drainTasks(1, TimeUnit.SECONDS)
        } catch (t: Throwable) {
            throw AssertionError(t)
        }
    }
    @Test
    fun basicFromNetwork() {
        val saved = AtomicReference<String>()
        shouldFetch = Objects::isNull
    }

}*/