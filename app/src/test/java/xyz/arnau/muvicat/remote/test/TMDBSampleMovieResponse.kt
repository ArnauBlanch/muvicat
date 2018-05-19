package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.tmdb.TMDBCastMember
import xyz.arnau.muvicat.remote.model.tmdb.TMDBCredits
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie

object TMDBSampleMovieResponse {
    val body = TMDBMovie(
        id = 438634,
        runtime = 96,
        vote_average = 6.8,
        vote_count = 39,
        credits = TMDBCredits(
            listOf(
                TMDBCastMember(
                    id = 1149067,
                    order = 0,
                    name = "David Verdaguer",
                    character = "Esteve",
                    profile_path = "/18Kb9Jt9JVsmdWOHUTuXTVMept2.jpg"
                ),
                TMDBCastMember(
                    id = 1743949,
                    order = 1,
                    name = "Bruna Cusí",
                    character = "Marga",
                    profile_path = "/pkbzOGgp7E0dT3t9Y2c27x9sJil.jpg"
                ),
                TMDBCastMember(
                    id = 1440534,
                    order = 2,
                    name = "Laia Artigas",
                    character = "Frida",
                    profile_path = "/32plYpIWKJdinDMbCwtsiuDz1Nm.jpg"
                )
            )
        )
    )

    val json: String = """
        {
            "adult": false,
            "backdrop_path": "/bhQXDHc2lCtI3EmjQFKh1BhvSK5.jpg",
            "belongs_to_collection": null,
            "budget": 960000,
            "genres": [
                {
                    "id": 18,
                    "name": "Drama"
                }
            ],
            "homepage": null,
            "id": 438634,
            "imdb_id": "tt5897636",
            "original_language": "ca",
            "original_title": "Estiu 1993",
            "overview": "After her mother's death, six-year-old Frida is sent to her uncle's family to live with them in the countryside. But Frida finds it hard to forget her mother and adapt to her new life.",
            "popularity": 5.29642,
            "poster_path": "/krdSl7IpDb9c31CXrzA5yfP1oYM.jpg",
            "production_companies": [
                {
                    "id": 63364,
                    "logo_path": null,
                    "name": "Inicia Films",
                    "origin_country": ""
                },
                {
                    "id": 26174,
                    "logo_path": null,
                    "name": "Avalon P.C.",
                    "origin_country": ""
                }
            ],
            "production_countries": [
                {
                    "iso_3166_1": "ES",
                    "name": "Spain"
                }
            ],
            "release_date": "2017-06-30",
            "revenue": 0,
            "runtime": 96,
            "spoken_languages": [
                {
                    "iso_639_1": "ca",
                    "name": "Català"
                }
            ],
            "status": "Released",
            "tagline": "A new family. A new world.",
            "title": "Summer 1993",
            "video": false,
            "vote_average": 6.8,
            "vote_count": 39,
            "credits": {
                "cast": [
                    {
                        "cast_id": 1,
                        "character": "Esteve",
                        "credit_id": "5892119ec3a3686b0a004cf8",
                        "gender": 2,
                        "id": 1149067,
                        "name": "David Verdaguer",
                        "order": 0,
                        "profile_path": "/18Kb9Jt9JVsmdWOHUTuXTVMept2.jpg"
                    },
                    {
                        "cast_id": 2,
                        "character": "Marga",
                        "credit_id": "589211acc3a3686b0a004d07",
                        "gender": 1,
                        "id": 1743949,
                        "name": "Bruna Cusí",
                        "order": 1,
                        "profile_path": "/pkbzOGgp7E0dT3t9Y2c27x9sJil.jpg"
                    },
                    {
                        "cast_id": 0,
                        "character": "Frida",
                        "credit_id": "589211949251412dc5009b37",
                        "gender": 0,
                        "id": 1440534,
                        "name": "Laia Artigas",
                        "order": 2,
                        "profile_path": "/32plYpIWKJdinDMbCwtsiuDz1Nm.jpg"
                    }
                ],
                "crew": [
                    {
                        "credit_id": "589211d29251412dd1009751",
                        "department": "Directing",
                        "gender": 0,
                        "id": 1748697,
                        "job": "Director",
                        "name": "Carla Simón",
                        "profile_path": "/mGtT725Qk14egXF9FYY8WSrfdcM.jpg"
                    },
                    {
                        "credit_id": "58a0bdab9251412b70001f0c",
                        "department": "Camera",
                        "gender": 0,
                        "id": 1096500,
                        "job": "Director of Photography",
                        "name": "Santiago Racaj",
                        "profile_path": null
                    }
                ]
            }
        }
    """.trimIndent()
}