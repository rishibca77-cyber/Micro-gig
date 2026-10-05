package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gigs")
data class GigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val businessName: String,
    val category: String,
    val payRate: String,
    val payAmountNumeric: Double,
    val isHourly: Boolean,
    val duration: String,
    val urgency: String,
    val location: String,
    val spotsAvailable: Int,
    val spotsFilled: Int = 0,
    val description: String,
    val requirements: String, // Pipe separated list of requirements
    val contactPhone: String,
    val contactEmail: String,
    val postedTime: String,
    val isVerifiedEmployer: Boolean = true,
    val isSaved: Boolean = false,
    val isUserPosted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
