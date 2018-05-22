package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.tmdb.TMDBGuestSessionResponse

object TMDBSampleGuestSessionResponse {
    val body = TMDBGuestSessionResponse(
        true,
        "GUEST_SESSION_ID"
    )

    val bodyNoSuccess = TMDBGuestSessionResponse(
        false,
        null
    )

    val bodySuccessWithNullId = TMDBGuestSessionResponse(
        true,
        null
    )


    val json: String = """
        {
            "success": true,
            "guest_session_id": "GUEST_SESSION_ID",
            "expires_at": "2018-05-22 15:17:01 UTC"
        }
    """.trimIndent()

    val jsonSuccessFalse: String = """
        {
            "success": false,
            "guest_session_id": null,
            "expires_at": null
        }
    """.trimIndent()

    val jsonSuccessNullId: String = """
        {
            "success": true,
            "guest_session_id": null,
            "expires_at": null
        }
    """.trimIndent()
}