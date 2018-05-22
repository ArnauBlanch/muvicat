package xyz.arnau.muvicat.remote.model.tmdb

data class TMDBGuestSessionResponse(
    var success: Boolean,
    var guest_session_id: String?
)