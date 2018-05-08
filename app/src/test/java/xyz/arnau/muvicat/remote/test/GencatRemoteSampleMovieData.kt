package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse

object GencatRemoteSampleMovieData {
    val body = GencatMovieResponse(
        listOf(
            GencatMovie(
                id = 12345,
                priority = 100,
                title = "Carinyo, jo soc tu",
                year = "2017",
                posterUrl = "carinyojosoctu.jpg",
                originalTitle = "L'un dans l'autre",
                direction = "Bruno Chiche",
                cast = "Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson",
                plot = "Dues parelles comparteixen amistat però tot canvia quan la Pénélope i el Pierre es converteixen en amants. Després d’una darrera nit junts, es desperten cadascun en el cos de l’altre.",
                releaseDate = "13/04/2018",
                originalLanguage = "francès",
                ageRating = "A partir de 12 anys",
                trailerUrl = "sbzncDh0a2s"
            ),
            GencatMovie(
                id = 31377,
                priority = 100,
                title = "Leo da Vinci, missió Mona Lisa",
                year = "2018",
                posterUrl = "leodavinci2.jpg",
                originalTitle = "Leo da Vinci: Missione Monna Lisa",
                direction = "Sergio Manfio",
                cast = "Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson",
                plot = "El jove Leo da Vinci té moltes idees al cap. Quan s’enamora de la Mona Lisa haurà d’aguditzar encara més el seu enginy. Junts viuran aventures emocionants i cercaran un tresor pirata.",
                releaseDate = "13/04/2018",
                originalLanguage = "anglès",
                ageRating = "Apta per a tots els públics",
                trailerUrl = "FQGe3jZEFBk"
            )
        )
    )

    val bodyWithNullId = GencatMovieResponse(
        listOf(
            GencatMovie(
                id = null,
                priority = 100,
                title = "Carinyo, jo soc tu",
                year = "2017",
                posterUrl = "carinyojosoctu.jpg",
                originalTitle = "L'un dans l'autre",
                direction = "Bruno Chiche",
                cast = "Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson",
                plot = "Dues parelles comparteixen amistat però tot canvia quan la Pénélope i el Pierre es converteixen en amants. Després d’una darrera nit junts, es desperten cadascun en el cos de l’altre.",
                releaseDate = "13/04/2018",
                originalLanguage = "francès",
                ageRating = "A partir de 12 anys",
                trailerUrl = "sbzncDh0a2s"
            ),
            GencatMovie(
                id = 31377,
                priority = 100,
                title = "Leo da Vinci, missió Mona Lisa",
                year = "2018",
                posterUrl = "leodavinci2.jpg",
                originalTitle = "Leo da Vinci: Missione Monna Lisa",
                direction = "Sergio Manfio",
                cast = "Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson",
                plot = "El jove Leo da Vinci té moltes idees al cap. Quan s’enamora de la Mona Lisa haurà d’aguditzar encara més el seu enginy. Junts viuran aventures emocionants i cercaran un tresor pirata.",
                releaseDate = "13/04/2018",
                originalLanguage = "anglès",
                ageRating = "Apta per a tots els públics",
                trailerUrl = "FQGe3jZEFBk"
            )
        )
    )

    val eTag: String = "\"8d569d-16589-56a479e141df0\""

    val xml: String = """
        <dataroot>
            <FILM>
                <IDFILM>12345</IDFILM>
                <PRIORITAT>100</PRIORITAT>
                <TITOL>Carinyo, jo soc tu</TITOL>
                <SITUACIO>--</SITUACIO>
                <ANY>2017</ANY>
                <CARTELL>carinyojosoctu.jpg</CARTELL>
                <ORIGINAL>L'un dans l'autre</ORIGINAL>
                <DIRECCIO>Bruno Chiche</DIRECCIO>
                <INTERPRETS>Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson</INTERPRETS>
                <SINOPSI>Dues parelles comparteixen amistat però tot canvia quan la Pénélope i el Pierre es converteixen en amants. Després d’una darrera nit junts, es desperten cadascun en el cos de l’altre.</SINOPSI>
                <VERSIO>Doblada i VOSC</VERSIO>
                <IDIOMA_x0020_ORIGINAL>francès</IDIOMA_x0020_ORIGINAL>
                <QUALIFICACIO>A partir de 12 anys</QUALIFICACIO>
                <TRAILER>sbzncDh0a2s</TRAILER>
                <WEB>--</WEB>
                <ESTRENA>13/04/2018</ESTRENA>
            </FILM>
            <FILM>
                <IDFILM>31377</IDFILM>
                <PRIORITAT>100</PRIORITAT>
                <TITOL>Leo da Vinci, missió Mona Lisa</TITOL>
                <SITUACIO>--</SITUACIO>
                <ANY>2018</ANY>
                <CARTELL>leodavinci2.jpg</CARTELL>
                <ORIGINAL>Leo da Vinci: Missione Monna Lisa</ORIGINAL>
                <DIRECCIO>Sergio Manfio</DIRECCIO>
                <INTERPRETS>Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson</INTERPRETS>
                <SINOPSI>El jove Leo da Vinci té moltes idees al cap. Quan s’enamora de la Mona Lisa haurà d’aguditzar encara més el seu enginy. Junts viuran aventures emocionants i cercaran un tresor pirata.</SINOPSI>
                <VERSIO>Doblada</VERSIO>
                <IDIOMA_x0020_ORIGINAL>anglès</IDIOMA_x0020_ORIGINAL>
                <QUALIFICACIO>Apta per a tots els públics</QUALIFICACIO>
                <TRAILER>FQGe3jZEFBk</TRAILER>
                <WEB>--</WEB>
                <ESTRENA>13/04/2018</ESTRENA>
            </FILM>
        </dataroot>
    """.trimIndent()
}