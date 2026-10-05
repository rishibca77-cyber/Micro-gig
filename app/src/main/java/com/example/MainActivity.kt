package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GigNavTab
import com.example.ui.GigViewModel
import com.example.ui.components.ApplyDialog
import com.example.ui.components.GigDetailSheet
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.PostGigScreen
import com.example.ui.screens.ResumeScreen
import com.example.ui.theme.GigAccent
import com.example.ui.theme.GigPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                QuickGigApp()
            }
        }
    }
}

@Composable
fun QuickGigApp(viewModel: GigViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar on messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    // BackHandler for secondary tabs or dialogs
    BackHandler(
        enabled = uiState.selectedGigForDetails != null ||
            uiState.gigToApply != null ||
            uiState.activeTab != GigNavTab.EXPLORE
    ) {
        when {
            uiState.gigToApply != null -> viewModel.closeApplyDialog()
            uiState.selectedGigForDetails != null -> viewModel.closeGigDetails()
            uiState.activeTab != GigNavTab.EXPLORE -> viewModel.setTab(GigNavTab.EXPLORE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Tab 1: Explore
                NavigationBarItem(
                    selected = uiState.activeTab == GigNavTab.EXPLORE,
                    onClick = { viewModel.setTab(GigNavTab.EXPLORE) },
                    icon = {
                        BadgedBox(badge = {
                            if (uiState.totalGigsCount > 0) {
                                Badge(containerColor = GigAccent) {
                                    Text(
                                        text = "${uiState.totalGigsCount}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Work, contentDescription = "Explore Gigs")
                        }
                    },
                    label = { Text("Gigs") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GigPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.testTag("nav_tab_explore")
                )

                // Tab 2: Applications & Saved
                NavigationBarItem(
                    selected = uiState.activeTab == GigNavTab.APPLICATIONS,
                    onClick = { viewModel.setTab(GigNavTab.APPLICATIONS) },
                    icon = {
                        BadgedBox(badge = {
                            val activeCount = uiState.applications.size
                            if (activeCount > 0) {
                                Badge(containerColor = GigPrimary) {
                                    Text(text = "$activeCount", fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Assignment, contentDescription = "My Work")
                        }
                    },
                    label = { Text("My Work") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GigPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.testTag("nav_tab_applications")
                )

                // Tab 3: Post a Gig
                NavigationBarItem(
                    selected = uiState.activeTab == GigNavTab.POST_GIG,
                    onClick = { viewModel.setTab(GigNavTab.POST_GIG) },
                    icon = {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Post a Gig")
                    },
                    label = { Text("Post Gig") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GigPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.testTag("nav_tab_post_gig")
                )

                // Tab 4: Worker Resume & Profile
                NavigationBarItem(
                    selected = uiState.activeTab == GigNavTab.RESUME,
                    onClick = { viewModel.setTab(GigNavTab.RESUME) },
                    icon = {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = "Worker Resume")
                    },
                    label = { Text("Resume") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GigPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.testTag("nav_tab_resume")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                GigNavTab.EXPLORE -> {
                    ExploreScreen(
                        uiState = uiState,
                        onSearchChange = { viewModel.onSearchQueryChange(it) },
                        onCategorySelect = { viewModel.onCategorySelect(it) },
                        onUrgencySelect = { viewModel.onUrgencySelect(it) },
                        onSortSelect = { viewModel.onSortSelect(it) },
                        onGigClick = { viewModel.openGigDetails(it) },
                        onApplyClick = { viewModel.openApplyDialog(it) },
                        onBookmarkToggle = { viewModel.toggleSaveGig(it) },
                        onNavigateToPostGig = { viewModel.setTab(GigNavTab.POST_GIG) }
                    )
                }

                GigNavTab.APPLICATIONS -> {
                    ApplicationsScreen(
                        uiState = uiState,
                        onGigClick = { viewModel.openGigDetails(it) },
                        onApplyClick = { viewModel.openApplyDialog(it) },
                        onBookmarkToggle = { viewModel.toggleSaveGig(it) },
                        onWithdrawApplication = { viewModel.withdrawApplication(it) },
                        onExploreClick = { viewModel.setTab(GigNavTab.EXPLORE) }
                    )
                }

                GigNavTab.POST_GIG -> {
                    PostGigScreen(
                        onPostGig = { title, biz, cat, payRate, payNum, dur, urgency, loc, spots, desc, reqs, phone, email ->
                            viewModel.postBusinessGig(
                                title = title,
                                businessName = biz,
                                category = cat,
                                payRate = payRate,
                                payNumeric = payNum,
                                duration = dur,
                                urgency = urgency,
                                location = loc,
                                spots = spots,
                                description = desc,
                                requirements = reqs,
                                phone = phone,
                                email = email
                            )
                        }
                    )
                }

                GigNavTab.RESUME -> {
                    ResumeScreen(
                        resume = uiState.resume,
                        onSaveResume = { name, headline, phone, email, loc, radius, bio, skills, exp, equip, imm, ev, wk ->
                            viewModel.saveResume(
                                fullName = name,
                                headline = headline,
                                phone = phone,
                                email = email,
                                location = loc,
                                radius = radius,
                                bio = bio,
                                skillsCsv = skills,
                                pastExperience = exp,
                                equipmentCsv = equip,
                                isImmediate = imm,
                                evenings = ev,
                                weekends = wk
                            )
                        }
                    )
                }
            }

            // Bottom Sheet for selected gig details
            uiState.selectedGigForDetails?.let { gig ->
                GigDetailSheet(
                    gig = gig,
                    isApplied = uiState.appliedGigIds.contains(gig.id),
                    onDismiss = { viewModel.closeGigDetails() },
                    onApplyClick = { viewModel.openApplyDialog(gig) },
                    onBookmarkToggle = { viewModel.toggleSaveGig(gig) }
                )
            }

            // Apply Dialog with attached resume
            uiState.gigToApply?.let { gig ->
                ApplyDialog(
                    gig = gig,
                    resume = uiState.resume,
                    onDismiss = { viewModel.closeApplyDialog() },
                    onSubmit = { coverNote ->
                        viewModel.submitApplication(coverNote)
                    }
                )
            }
        }
    }
}
