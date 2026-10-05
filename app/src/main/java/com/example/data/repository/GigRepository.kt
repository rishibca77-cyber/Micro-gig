package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.ApplicationEntity
import com.example.data.local.GigEntity
import com.example.data.local.ResumeEntity
import com.example.data.seed.SeedData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GigRepository(
    private val database: AppDatabase,
    private val firestore: FirebaseFirestore? = null
) {
    private val gigDao = database.gigDao()
    private val appDao = database.applicationDao()
    private val resumeDao = database.resumeDao()

    val allGigs: Flow<List<GigEntity>> = gigDao.getAllGigs()
    val savedGigs: Flow<List<GigEntity>> = gigDao.getSavedGigs()
    val allApplications: Flow<List<ApplicationEntity>> = appDao.getAllApplications()
    val appliedGigIds: Flow<List<Long>> = appDao.getAppliedGigIds()
    val userResume: Flow<ResumeEntity?> = resumeDao.getResume()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = gigDao.getGigCount()
        if (count == 0) {
            gigDao.insertGigs(SeedData.initialGigs)
        }
        val existingResume = resumeDao.getResumeSync()
        if (existingResume == null) {
            resumeDao.insertOrUpdateResume(ResumeEntity())
        }
    }

    suspend fun toggleSaved(gigId: Long, currentSaved: Boolean) = withContext(Dispatchers.IO) {
        gigDao.updateSavedStatus(gigId, !currentSaved)
    }

    suspend fun submitApplication(
        gig: GigEntity,
        applicantName: String,
        applicantPhone: String,
        applicantEmail: String,
        coverNote: String
    ): Long = withContext(Dispatchers.IO) {
        val application = ApplicationEntity(
            gigId = gig.id,
            gigTitle = gig.title,
            businessName = gig.businessName,
            payRate = gig.payRate,
            location = gig.location,
            applicantName = applicantName,
            applicantPhone = applicantPhone,
            applicantEmail = applicantEmail,
            coverNote = coverNote,
            status = "PENDING",
            appliedAt = System.currentTimeMillis()
        )
        val id = appDao.insertApplication(application)
        gigDao.incrementSpotsFilled(gig.id)

        // Sync with Firestore if authenticated
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (firestore != null && currentUserId != null) {
            try {
                val appMap = mapOf(
                    "gigId" to gig.id.toString(),
                    "gigTitle" to gig.title,
                    "businessName" to gig.businessName,
                    "payRate" to gig.payRate,
                    "location" to gig.location,
                    "applicantId" to currentUserId,
                    "applicantName" to applicantName,
                    "applicantPhone" to applicantPhone,
                    "applicantEmail" to applicantEmail,
                    "coverNote" to coverNote,
                    "status" to "PENDING",
                    "appliedAt" to System.currentTimeMillis()
                )
                firestore.collection("applications").document(id.toString()).set(appMap).await()
            } catch (e: Exception) {
                Log.w("GigRepository", "Firestore application sync skipped: ${e.message}")
            }
        }
        id
    }

    suspend fun postGig(gig: GigEntity): Long = withContext(Dispatchers.IO) {
        val id = gigDao.insertGig(gig)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (firestore != null && currentUserId != null) {
            try {
                val gigMap = mapOf(
                    "title" to gig.title,
                    "businessName" to gig.businessName,
                    "category" to gig.category,
                    "payRate" to gig.payRate,
                    "payAmountNumeric" to gig.payAmountNumeric,
                    "isHourly" to gig.isHourly,
                    "duration" to gig.duration,
                    "urgency" to gig.urgency,
                    "location" to gig.location,
                    "spotsAvailable" to gig.spotsAvailable,
                    "spotsFilled" to 0,
                    "description" to gig.description,
                    "requirements" to gig.requirements,
                    "contactPhone" to gig.contactPhone,
                    "contactEmail" to gig.contactEmail,
                    "postedTime" to gig.postedTime,
                    "posterId" to currentUserId,
                    "isVerifiedEmployer" to true,
                    "isUserPosted" to true,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("gigs").document(id.toString()).set(gigMap).await()
            } catch (e: Exception) {
                Log.w("GigRepository", "Firestore gig sync skipped: ${e.message}")
            }
        }
        id
    }

    suspend fun saveResume(resume: ResumeEntity) = withContext(Dispatchers.IO) {
        resumeDao.insertOrUpdateResume(resume)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (firestore != null && currentUserId != null) {
            try {
                val resumeMap = mapOf(
                    "userId" to currentUserId,
                    "fullName" to resume.fullName,
                    "headline" to resume.headline,
                    "phone" to resume.phone,
                    "email" to resume.email,
                    "location" to resume.location,
                    "serviceRadiusMiles" to resume.serviceRadiusMiles,
                    "bio" to resume.bio,
                    "skillsCsv" to resume.skillsCsv,
                    "pastExperience" to resume.pastExperience,
                    "equipmentCsv" to resume.equipmentCsv,
                    "isAvailableImmediately" to resume.isAvailableImmediately,
                    "availableEvenings" to resume.availableEvenings,
                    "availableWeekends" to resume.availableWeekends,
                    "rating" to resume.rating,
                    "completedGigsCount" to resume.completedGigsCount
                )
                firestore.collection("resumes").document(currentUserId).set(resumeMap).await()
            } catch (e: Exception) {
                Log.w("GigRepository", "Firestore resume sync skipped: ${e.message}")
            }
        }
    }

    suspend fun getResumeSync(): ResumeEntity? = withContext(Dispatchers.IO) {
        resumeDao.getResumeSync()
    }

    suspend fun updateApplicationStatus(id: Long, newStatus: String) = withContext(Dispatchers.IO) {
        appDao.updateStatus(id, newStatus)
    }

    suspend fun withdrawApplication(id: Long) = withContext(Dispatchers.IO) {
        appDao.deleteApplication(id)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (firestore != null && currentUserId != null) {
            try {
                firestore.collection("applications").document(id.toString()).delete().await()
            } catch (e: Exception) {
                Log.w("GigRepository", "Firestore application deletion skipped: ${e.message}")
            }
        }
    }
}
