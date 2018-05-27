package xyz.arnau.muvicat.remote.model.gencat

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "dataroot", strict = false)
data class GencatShowingResponse(
    @field:ElementList(name = "SESSIONS", inline = true)
    var showingList: List<GencatShowing>? = null
)
