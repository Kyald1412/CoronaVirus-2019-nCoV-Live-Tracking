package co.kyald.coronavirustracking.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DummyEntity(
    @PrimaryKey
    val id: Int,
    val value: String
)


