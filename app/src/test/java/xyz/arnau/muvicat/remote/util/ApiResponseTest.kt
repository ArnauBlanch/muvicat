package xyz.arnau.muvicat.remote.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response
import okhttp3.Headers
import okhttp3.ResponseBody
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import java.io.IOException
import java.net.HttpURLConnection.*

@Suppress("DEPRECATION")
class ApiResponseTest {
    @Test
    fun testSetters() {
        val throwable = Throwable("ERROR MESSAGE")
        val apiResponse = ApiResponse<String>(throwable)
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
        val response = mockResponse(HTTP_BAD_REQUEST, errorBodyException = exception)
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

    private fun mockResponse(code: Int, body: String? = null, etag: String? = null,
                             errorBody: String? = null, message: String? = null,
                             errorBodyException: IOException? = null): Response<*> {
        val response: Response<*> = mock(Response::class.java)
        `when`(response.code()).thenReturn(code)
        if (code in 200..299) {
            `when`(response.isSuccessful).thenReturn(true)
        }
        `when`(response.body()).thenReturn(body)
        val headers = mock(Headers::class.java)
        `when`(response.headers()).thenReturn(headers)
        `when`(headers.get("Etag")).thenReturn(etag)
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