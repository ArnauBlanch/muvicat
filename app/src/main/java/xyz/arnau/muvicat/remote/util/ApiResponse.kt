package xyz.arnau.muvicat.remote.util

import retrofit2.Response
import timber.log.Timber
import xyz.arnau.muvicat.remote.model.ResponseStatus
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_SERVER_ERROR

@Suppress("DEPRECATION")
/**
 * Common class used by API responses.
 * @param <T>
</T> */
class ApiResponse<T> {
    val code: Int
    var body: T? = null
        private set
    var eTag: String? = null
        private set
    var errorMessage: String? = null
        private set
    var status: ResponseStatus = ResponseStatus.ERROR
        private set

    constructor(error: Throwable) {
        code = HTTP_SERVER_ERROR
        errorMessage = error.message
        status = ResponseStatus.ERROR
    }

    constructor(response: Response<T>) {
        code = response.code()
        when {
            response.isSuccessful -> {
                body = response.body()
                eTag = response.headers().get("Etag")
                status = ResponseStatus.SUCCESSFUL
            }
            code == HTTP_NOT_MODIFIED -> status = ResponseStatus.NOT_MODIFIED
            else -> {
                var message: String? = null
                if (response.errorBody() != null) {
                    try {
                        message = response.errorBody()!!.string()
                    } catch (ignored: IOException) {
                        Timber.e(ignored, "error while parsing response")
                    }

                }
                if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                    message = response.message()
                }
                errorMessage = message
                status = ResponseStatus.ERROR
            }
        }
    }
}