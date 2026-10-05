package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.auth.AuthManager
import com.example.data.local.AppDatabase
import com.example.data.local.ApplicationEntity
import com.example.data.local.GigEntity
import com.example.data.local.ResumeEntity
import com.example.data.model.GigCategory
import com.example.data.model.UrgencyLevel
import com.example.data.repository.GigRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GigNavTab(val label: String) {
    EXPLORE("Explore Gigs"),
    APPLICATIONS("My Work"),
    POST_GIG("Post a Gig"),
    RESUME("My Resume")
}

enum class GigSort(val label: String) {
    NEWEST("Newest First"),
    PAY_HIGH_TO_LOW("Highest Pay"),
    URGENT_FIRST("Urgent First")
}

data class GigUiState(
    val currentUser: FirebaseUser? = null,
    val isSigningIn: Boolean = false,
    val authError: String? = null,
    val filteredGigs: List<GigEntity> = emptyList(),
    val totalGigsCount: Int = 0,
    val savedGigs: List<GigEntity> = emptyList(),
    val applications: List<ApplicationEntity> = emptyList(),
    val appliedGigIds: Set<Long> = emptySet(),
    val resume: ResumeEntity? = null,
    val activeTab: GigNavTab = GigNavTab.EXPLORE,
    val searchQuery: String = "",
    val selectedCategory: GigCategory = GigCategory.ALL,
    val selectedUrgency: UrgencyLevel? = null,
    val selectedSort: GigSort = GigSort.NEWEST,
    val selectedGigForDetails: GigEntity? = null,
    val gigToApply: GigEntity? = null,
    val snackbarMessage: String? = null
)

private data class RepoData(
    val allGigs: List<GigEntity> = emptyList(),
    val savedGigs: List<GigEntity> = emptyList(),
    val applications: List<ApplicationEntity> = emptyList(),
    val appliedGigIds: List<Long> = emptyList(),
    val resume: ResumeEntity? = null
)

private data class FilterData(
    val query: String = "",
    val category: GigCategory = GigCategory.ALL,
    val urgency: UrgencyLevel? = null,
    val sort: GigSort = GigSort.NEWEST
)

class GigViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val repository: GigRepository

    private val _isSigningIn = MutableStateFlow(false)
    private val _authError = MutableStateFlow<String?>(null)
    private val _activeTab = MutableStateFlow(GigNavTab.EXPLORE)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(GigCategory.ALL)
    private val _selectedUrgency = MutableStateFlow<UrgencyLevel?>(null)
    private val _selectedSort = MutableStateFlow(GigSort.NEWEST)
    private val _selectedGigForDetails = MutableStateFlow<GigEntity?>(null)
    private val _gigToApply = MutableStateFlow<GigEntity?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    init {
        val database = AppDatabase.getInstance(application)
        val databaseId = application.getString(R.string.firestore_database_id)
        val firestore = try {
            FirebaseFirestore.getInstance(databaseId)
        } catch (e: Exception) {
            null
        }
        repository = GigRepository(database, firestore)
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    private val repoDataFlow = combine(
        repository.allGigs,
        repository.savedGigs,
        repository.allApplications,
        repository.appliedGigIds,
        repository.userResume
    ) { allGigs, savedGigs, apps, appliedIds, resume ->
        RepoData(allGigs, savedGigs, apps, appliedIds, resume)
    }

    private val filterDataFlow = combine(
        _searchQuery,
        _selectedCategory,
        _selectedUrgency,
        _selectedSort
    ) { query, category, urgency, sort ->
        FilterData(query, category, urgency, sort)
    }

    val uiState: StateFlow<GigUiState> = combine(
        authManager.authStateFlow(),
        _isSigningIn,
        _authError,
        repoDataFlow,
        filterDataFlow
    ) { user, isSigningIn, authError, repoData, filters ->
        val filtered = repoData.allGigs.filter { gig ->
            val matchesQuery = filters.query.isBlank() ||
                gig.title.contains(filters.query, ignoreCase = true) ||
                gig.businessName.contains(filters.query, ignoreCase = true) ||
                gig.description.contains(filters.query, ignoreCase = true) ||
                gig.location.contains(filters.query, ignoreCase = true) ||
                gig.category.contains(filters.query, ignoreCase = true)

            val matchesCategory = filters.category == GigCategory.ALL ||
                gig.category.equals(filters.category.displayName, ignoreCase = true)

            val matchesUrgency = filters.urgency == null || gig.urgency == filters.urgency.name

            matchesQuery && matchesCategory && matchesUrgency
        }.sortedWith { a, b ->
            when (filters.sort) {
                GigSort.NEWEST -> b.id.compareTo(a.id)
                GigSort.PAY_HIGH_TO_LOW -> b.payAmountNumeric.compareTo(a.payAmountNumeric)
                GigSort.URGENT_FIRST -> {
                    val priorityA = if (a.urgency == "URGENT_TODAY") 0 else 1
                    val priorityB = if (b.urgency == "URGENT_TODAY") 0 else 1
                    priorityA.compareTo(priorityB)
                }
            }
        }

        GigUiState(
            currentUser = user,
            isSigningIn = isSigningIn,
            authError = authError,
            filteredGigs = filtered,
            totalGigsCount = repoData.allGigs.size,
            savedGigs = repoData.savedGigs,
            applications = repoData.applications,
            appliedGigIds = repoData.appliedGigIds.toSet(),
            resume = repoData.resume,
            activeTab = _activeTab.value,
            searchQuery = filters.query,
            selectedCategory = filters.category,
            selectedUrgency = filters.urgency,
            selectedSort = filters.sort,
            selectedGigForDetails = _selectedGigForDetails.value,
            gigToApply = _gigToApply.value,
            snackbarMessage = _snackbarMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GigUiState(currentUser = authManager.currentUser)
    )

    fun signInWithGoogle() {
        viewModelScope.launch {
            _isSigningIn.value = true
            _authError.value = null
            val result = authManager.signInWithGoogle()
            _isSigningIn.value = false
            result.onSuccess { user ->
                _snackbarMessage.value = "Welcome back, ${user.displayName ?: "Worker"}!"
                // Update resume name if default
                val currentResume = uiState.value.resume
                if (currentResume != null && user.displayName != null && currentResume.fullName == "Alex Rivera") {
                    repository.saveResume(
                        currentResume.copy(
                            fullName = user.displayName ?: currentResume.fullName,
                            email = user.email ?: currentResume.email
                        )
                    )
                }
            }.onFailure { error ->
                _authError.value = "Sign-in cancelled or failed: ${error.localizedMessage}"
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _snackbarMessage.value = "Signed out"
    }

    fun setTab(tab: GigNavTab) {
        _activeTab.value = tab
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: GigCategory) {
        _selectedCategory.value = category
    }

    fun onUrgencySelect(urgency: UrgencyLevel?) {
        _selectedUrgency.value = urgency
    }

    fun onSortSelect(sort: GigSort) {
        _selectedSort.value = sort
    }

    fun openGigDetails(gig: GigEntity) {
        _selectedGigForDetails.value = gig
    }

    fun closeGigDetails() {
        _selectedGigForDetails.value = null
    }

    fun openApplyDialog(gig: GigEntity) {
        _gigToApply.value = gig
    }

    fun closeApplyDialog() {
        _gigToApply.value = null
    }

    fun toggleSaveGig(gig: GigEntity) {
        viewModelScope.launch {
            repository.toggleSaved(gig.id, gig.isSaved)
            val msg = if (gig.isSaved) "Removed from saved gigs" else "Saved to Bookmarks"
            _snackbarMessage.value = msg
        }
    }

    fun submitApplication(note: String) {
        val gig = _gigToApply.value ?: return
        val currentResume = uiState.value.resume ?: ResumeEntity()
        viewModelScope.launch {
            repository.submitApplication(
                gig = gig,
                applicantName = currentResume.fullName,
                applicantPhone = currentResume.phone,
                applicantEmail = currentResume.email,
                coverNote = note
            )
            _gigToApply.value = null
            _selectedGigForDetails.value = null
            _snackbarMessage.value = "Application sent to ${gig.businessName}! Check 'My Work' tab."
        }
    }

    fun postBusinessGig(
        title: String,
        businessName: String,
        category: String,
        payRate: String,
        payNumeric: Double,
        duration: String,
        urgency: String,
        location: String,
        spots: Int,
        description: String,
        requirements: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            val gig = GigEntity(
                title = title.trim(),
                businessName = businessName.trim(),
                category = category,
                payRate = payRate.trim(),
                payAmountNumeric = payNumeric,
                isHourly = payRate.contains("/hr", ignoreCase = true),
                duration = duration.trim(),
                urgency = urgency,
                location = location.trim(),
                spotsAvailable = spots,
                spotsFilled = 0,
                description = description.trim(),
                requirements = requirements.trim(),
                contactPhone = phone.trim(),
                contactEmail = email.trim(),
                postedTime = "Just now",
                isVerifiedEmployer = true,
                isUserPosted = true
            )
            repository.postGig(gig)
            _activeTab.value = GigNavTab.EXPLORE
            _snackbarMessage.value = "Micro gig posted to Firestore! Now live in the portal."
        }
    }

    fun saveResume(
        fullName: String,
        headline: String,
        phone: String,
        email: String,
        location: String,
        radius: Int,
        bio: String,
        skillsCsv: String,
        pastExperience: String,
        equipmentCsv: String,
        isImmediate: Boolean,
        evenings: Boolean,
        weekends: Boolean
    ) {
        viewModelScope.launch {
            val updated = ResumeEntity(
                id = 1,
                fullName = fullName.trim(),
                headline = headline.trim(),
                phone = phone.trim(),
                email = email.trim(),
                location = location.trim(),
                serviceRadiusMiles = radius,
                bio = bio.trim(),
                skillsCsv = skillsCsv.trim(),
                pastExperience = pastExperience.trim(),
                equipmentCsv = equipmentCsv.trim(),
                isAvailableImmediately = isImmediate,
                availableEvenings = evenings,
                availableWeekends = weekends
            )
            repository.saveResume(updated)
            _snackbarMessage.value = "Worker Resume & Profile synced to Firestore!"
        }
    }

    fun withdrawApplication(appId: Long) {
        viewModelScope.launch {
            repository.withdrawApplication(appId)
            _snackbarMessage.value = "Application withdrawn."
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
