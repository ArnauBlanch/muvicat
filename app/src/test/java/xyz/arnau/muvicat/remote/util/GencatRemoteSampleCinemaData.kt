package xyz.arnau.muvicat.remote.util

import xyz.arnau.muvicat.remote.model.GencatCinema
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse

object GencatRemoteSampleCinemaData {
    val body = GencatCinemaResponse(
        listOf(
            GencatCinema(
                id = 37147,
                name = "Cinemes Girona",
                address = "C. de Girona, 173, 08025 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            ),
            GencatCinema(
                id = 37150,
                name = "Cinesa Diagonal",
                address = "Carrer de Santa Fe de Nou Mèxic 08021 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            ),
            GencatCinema(
                id = 37153,
                name = "Filmoteca de Catalunya",
                address = "Pl. de Salvador Seguí, 1-9, 08001 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            )
        )
    )

    val bodyWithNullId = GencatCinemaResponse(
        listOf(
            GencatCinema(
                id = null,
                name = "Cinemes Girona",
                address = "C. de Girona, 173, 08025 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            ),
            GencatCinema(
                id = 37150,
                name = "Cinesa Diagonal",
                address = "Carrer de Santa Fe de Nou Mèxic 08021 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            ),
            GencatCinema(
                id = 37153,
                name = "Filmoteca de Catalunya",
                address = "Pl. de Salvador Seguí, 1-9, 08001 Barcelona",
                town = "Barcelona",
                region = "Barcelonès",
                province = "BARCELONA"
            )
        )
    )

    val eTag: String = "\"8d569d-16589-56a479e141df0\""

    val xml: String = """
        <dataroot>
            <CINEMES>
                <CINEID>37147</CINEID>
                <CINENOM>Cinemes Girona</CINENOM>
                <CINEADRECA>C. de Girona, 173, 08025 Barcelona</CINEADRECA>
                <LOCALITAT>Barcelona</LOCALITAT>
                <COMARCA>Barcelonès</COMARCA>
                <PROVINCIA>BARCELONA</PROVINCIA>
            </CINEMES>
            <CINEMES>
                <CINEID>37150</CINEID>
                <CINENOM>Cinesa Diagonal</CINENOM>
                <CINEADRECA>Carrer de Santa Fe de Nou Mèxic 08021 Barcelona</CINEADRECA>
                <LOCALITAT>Barcelona</LOCALITAT>
                <COMARCA>Barcelonès</COMARCA>
                <PROVINCIA>BARCELONA</PROVINCIA>
            </CINEMES>
            <CINEMES>
                <CINEID>37153</CINEID>
                <CINENOM>Filmoteca de Catalunya</CINENOM>
                <CINEADRECA>Pl. de Salvador Seguí, 1-9, 08001 Barcelona</CINEADRECA>
                <LOCALITAT>Barcelona</LOCALITAT>
                <COMARCA>Barcelonès</COMARCA>
                <PROVINCIA>BARCELONA</PROVINCIA>
            </CINEMES>
        </dataroot>
    """.trimIndent()
}