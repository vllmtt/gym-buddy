package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.arch.persistence.room.Room
import android.content.Context
import fi.anttonen.villematti.apps.gymbuddy.model.AppDatabase

object DataSource {
    fun initDatabse(applicationContext: Context) {
        (DATA_SOURCE as? RoomDataSource)?.init(applicationContext)
    }

    val DATA_SOURCE: GymEntriesDataSource? = RoomDataSource()
}