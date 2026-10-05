package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey
    val id: Long = 1,
    val fullName: String = "Alex Rivera",
    val headline: String = "Reliable On-Demand Helper & Event Crew",
    val phone: String = "(555) 234-8971",
    val email: String = "alex.rivera.work@example.com",
    val location: String = "Downtown Metro Area",
    val serviceRadiusMiles: Int = 15,
    val bio: String = "Dedicated, punctual micro-tasker with experience in retail stocking, moving assistance, event staging, and light maintenance. Fast learner with reliable transportation.",
    val skillsCsv: String = "Heavy Lifting, Furniture Assembly, Event Setup, Customer Support, Inventory Count, Pressure Washing, Driver's License",
    val pastExperience: String = "• Event Setup Staff at Grandview Center (10+ gigs)\n• Moving Assistant for residential relocations\n• Retail Inventory Associate during holiday peak season",
    val equipmentCsv: String = "Pickup Truck, Hand Dolly, Tool Set, Work Gloves, Steel-toe Boots",
    val isAvailableImmediately: Boolean = true,
    val availableEvenings: Boolean = true,
    val availableWeekends: Boolean = true,
    val rating: Double = 4.9,
    val completedGigsCount: Int = 18
)
