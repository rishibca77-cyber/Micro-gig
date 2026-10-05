package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes WHERE id = 1 LIMIT 1")
    fun getResume(): Flow<ResumeEntity?>

    @Query("SELECT * FROM resumes WHERE id = 1 LIMIT 1")
    suspend fun getResumeSync(): ResumeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResume(resume: ResumeEntity)
}
