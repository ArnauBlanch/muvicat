package xyz.arnau.muvicat.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "cast_members", indices = [(Index(value = ["id"], name = "castMemberId"))])
data class CastMemberEntity(
    @PrimaryKey
    val id: Int,
    val order: Int?,
    val name: String,
    val character: String?,
    val profile_path: String?
)