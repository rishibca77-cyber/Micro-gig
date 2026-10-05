package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ApplicationEntity
import com.example.data.local.GigEntity
import com.example.data.local.ResumeEntity
import com.example.data.seed.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GigRepository(private val database: AppDatabase) {
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
        id
    }

    suspend fun postGig(gig: GigEntity): Long = withContext(Dispatchers.IO) {
        gigDao.insertGig(gig)
    }

    suspend fun saveResume(resume: ResumeEntity) = withContext(Dispatchers.IO) {
        resumeDao.insertOrUpdateResume(resume)
    }

    suspend fun getResumeSync(): ResumeEntity? = withContext(Dispatchers.IO) {
        resumeDao.getResumeSync()
    }

    suspend fun updateApplicationStatus(id: Long, newStatus: String) = withContext(Dispatchers.IO) {
        appDao.updateStatus(id, newStatus)
    }

    suspend fun withdrawApplication(id: Long) = withContext(Dispatchers.IO) {
        appDao.deleteApplication(id)
    }
}
