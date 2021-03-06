package xyz.arnau.muvicat.remote.utils

import junit.framework.TestCase.assertEquals
import okhttp3.Headers
import okhttp3.ResponseBody
import okhttp3.internal.Util
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import java.io.IOException
import java.net.HttpURLConnection.*

@Suppress("DEPRECATION")
class ApiResponseTest {
    @Test
    fun testSetters() {
        val throwable = Throwable("ERROR MESSAGE")
        ApiResponse<String>(throwable)
    }

    @Test
    fun constructFromThrowable() {
        val throwable = Throwable("ERROR MESSAGE")
        val apiResponse = ApiResponse<Int>(throwable)

        assertEquals(HTTP_SERVER_ERROR, apiResponse.code)
        assertEquals(throwable.message, apiResponse.errorMessage)
        assertEquals(ERROR, apiResponse.status)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
    }

    @Test
    fun constructFromSuccesfulResponse() {
        val response = mockResponse(HTTP_OK, body = "BODY", etag = "ETAG")
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_OK, apiResponse.code)
        assertEquals("BODY", apiResponse.body)
        assertEquals("ETAG", apiResponse.eTag)
        assertEquals(null, apiResponse.errorMessage)
        assertEquals(SUCCESSFUL, apiResponse.status)
    }

    @Test
    fun constructFromNotModifiedResponse() {
        val response = mockResponse(HTTP_NOT_MODIFIED)
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_NOT_MODIFIED, apiResponse.code)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
        assertEquals(null, apiResponse.errorMessage)
        assertEquals(NOT_MODIFIED, apiResponse.status)
    }

    @Test
    fun constructFromErrorResponseWithNotNullErrorBody() {
        val response = mockResponse(HTTP_BAD_REQUEST, errorBody = "ERROR BODY")
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_BAD_REQUEST, apiResponse.code)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
        assertEquals("ERROR BODY", apiResponse.errorMessage)
        assertEquals(ERROR, apiResponse.status)
    }

    @Test
    fun constructFromErrorResponseWithNotNullUnparseableErrorBody() {
        val exception = mock(IOException::class.java)
        val response = mockResponse(HTTP_BAD_REQUEST, errorBody = "error", errorBodyException = exception)
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_BAD_REQUEST, apiResponse.code)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
        assertEquals(null, apiResponse.errorMessage)
        assertEquals(ERROR, apiResponse.status)
    }

    @Test
    fun constructFromErrorResponseWithNullErrorBody() {
        val response = mockResponse(HTTP_BAD_REQUEST, errorBody = null)
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_BAD_REQUEST, apiResponse.code)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
        assertEquals(null, apiResponse.errorMessage)
        assertEquals(ERROR, apiResponse.status)
    }

    @Test
    fun constructFromErrorResponseWithNullErrorBody_withChange() {
        val response = mockResponse(HTTP_BAD_REQUEST, errorBody = null)
        val apiResponse = ApiResponse(response)

        assertEquals(HTTP_BAD_REQUEST, apiResponse.code)
        assertEquals(null, apiResponse.body)
        assertEquals(null, apiResponse.eTag)
        assertEquals(null, apiResponse.errorMessage)
        assertEquals(ERROR, apiResponse.status)
    }

    @Test
    fun testSettersForCoverage() {
        val response = mockResponse(500)
        val apiResponse = ApiResponse(response)
        apiResponse.body = null
        apiResponse.eTag = null
        apiResponse.errorMessage = null
        apiResponse.status = NOT_MODIFIED
    }

    companion object {
        fun mockResponse(
            code: Int, body: String? = null, etag: String? = null,
            errorBody: String? = null, message: String? = null,
            errorBodyException: IOException? = null
        ): Response<*> {
            val response: Response<*> = mock(Response::class.java)
            `when`(response.code()).thenReturn(code)
            if (code in 200..299) {
                `when`(response.isSuccessful).thenReturn(true)
            }
            `when`(response.body()).thenReturn(body)
            val headers = mock(Headers::class.java)
            `when`(response.headers()).thenReturn(headers)
            `when`(headers.get("ETag")).thenReturn(etag)
            val responseErrorBody = mock(ResponseBody::class.java)
            if (errorBodyException != null) {
                `when`(responseErrorBody.string()).thenThrow(errorBodyException)
            } else {
                `when`(responseErrorBody.string()).thenReturn(errorBody)
            }
            if (errorBody != null) {
                `when`(response.errorBody()).thenReturn(responseErrorBody)
            }
            `when`(response.message()).thenReturn(message)

            return response
        }
    }
}