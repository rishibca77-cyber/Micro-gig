package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GigDao {
    @Query("SELECT * FROM gigs ORDER BY isUserPosted DESC, id ASC")
    fun getAllGigs(): Flow<List<GigEntity>>

    @Query("SELECT * FROM gigs WHERE id = :id LIMIT 1")
    suspend fun getGigById(id: Long): GigEntity?

    @Query("SELECT * FROM gigs WHERE isSaved = 1 ORDER BY id DESC")
    fun getSavedGigs(): Flow<List<GigEntity>>

    @Query("SELECT COUNT(*) FROM gigs")
    suspend fun getGigCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGigs(gigs: List<GigEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGig(gig: GigEntity): Long

    @Update
    suspend fun updateGig(gig: GigEntity)

    @Query("UPDATE gigs SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    @Query("UPDATE gigs SET spotsFilled = spotsFilled + 1 WHERE id = :id AND spotsFilled < spotsAvailable")
    suspend fun incrementSpotsFilled(id: Long)
}
