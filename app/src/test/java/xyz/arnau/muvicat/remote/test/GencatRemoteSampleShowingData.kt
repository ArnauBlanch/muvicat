package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.gencat.GencatShowing
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse

object GencatRemoteSampleShowingData {
    val body = GencatShowingResponse(
        listOf(
            GencatShowing(
                movieId = 31123,
                cinemaId = 37406,
                date = "08/05/2018",
                version = "VOSC",
                seasonId = 0
            ),
            GencatShowing(
                movieId = 31484,
                cinemaId = 37241,
                date = "09/05/2018",
                version = "VOSC",
                seasonId = 57142
            )
        )
    )

    val bodyWithNullId = GencatShowingResponse(
        listOf(
            GencatShowing(
                movieId = 31123,
                cinemaId = 37406,
                date = "08/05/2018",
                version = "VOSC",
                seasonId = 0
            ),
            GencatShowing(
                movieId = null,
                cinemaId = 37241,
                date = "09/05/2018",
                version = "VOSC",
                seasonId = 57142
            )
        )
    )

    const val eTag: String = "\"8d569d-16589-56a479e141df0\""

    val xml: String = """
        <dataroot>
            <SESSIONS>
                <IDFILM>31123</IDFILM>
                <ses_id>1</ses_id>
                <CINEID>37406</CINEID>
                <TITOL>Amor a la siciliana</TITOL>
                <ses_data>08/05/2018</ses_data>
                <CINENOM>Cinemes Texas</CINENOM>
                <LOCALITAT>Barcelona</LOCALITAT>
                <COMARCA>Barcelonès</COMARCA>
                <CICLEID>0</CICLEID>
                <ver>VOSC</ver>
                <preu>0</preu>
                <ORDRESESSIO>20180508</ORDRESESSIO>
            </SESSIONS>
            <SESSIONS>
                <IDFILM>31484</IDFILM>
                <ses_id>1</ses_id>
                <CINEID>37241</CINEID>
                <TITOL>Un home millor</TITOL>
                <ses_data>09/05/2018</ses_data>
                <CINENOM>Casal de Cultura de Sant Cugat del Vallès</CINENOM>
                <LOCALITAT>Sant Cugat del Vallès</LOCALITAT>
                <COMARCA>Vallès Occidental</COMARCA>
                <CICLEID>57142</CICLEID>
                <ver>VOSC</ver>
                <preu>0</preu>
                <ORDRESESSIO>20180509</ORDRESESSIO>
            </SESSIONS>
        </dataroot>
    """.trimIndent()
}