package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class CastMember(
    @PrimaryKey
    val id: Int,
    val order: Int?,
    val name: String,
    val character: String?,
    val profile_path: String?
)