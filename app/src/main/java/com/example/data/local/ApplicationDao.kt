package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY appliedAt DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE gigId = :gigId LIMIT 1")
    suspend fun getApplicationForGig(gigId: Long): ApplicationEntity?

    @Query("SELECT gigId FROM applications")
    fun getAppliedGigIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity): Long

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplication(id: Long)
}
