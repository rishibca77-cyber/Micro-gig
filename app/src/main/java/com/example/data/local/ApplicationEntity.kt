package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gigId: Long,
    val gigTitle: String,
    val businessName: String,
    val payRate: String,
    val location: String,
    val applicantName: String,
    val applicantPhone: String,
    val applicantEmail: String,
    val coverNote: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, COMPLETED
    val appliedAt: Long = System.currentTimeMillis()
)
