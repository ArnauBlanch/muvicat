package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.tmdb.*

object TMDBSampleStatusResponse {
    val successBody = TMDBStatusResponse(
        1,
        "Success."
    )

    val errorBody = TMDBStatusResponse(
        34,
        "The resource you requested could not be found."
    )

    val successJson: String = """
        {
            "status_code": 1,
            "status_message": "Success."
        }
    """.trimIndent()

    val errorJson: String = """
        {
            "status_message": "The resource you requested could not be found.",
            "status_code": 34
        }
        """.trimIndent()
}