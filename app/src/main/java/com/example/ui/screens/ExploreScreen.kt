package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GigEntity
import com.example.data.model.GigCategory
import com.example.data.model.UrgencyLevel
import com.example.ui.GigSort
import com.example.ui.GigUiState
import com.example.ui.components.GigCard
import com.example.ui.theme.GigAccent
import com.example.ui.theme.GigAccentContainer
import com.example.ui.theme.GigOnAccentContainer
import com.example.ui.theme.GigPrimary
import com.example.ui.theme.GigPrimaryContainer

@Composable
fun ExploreScreen(
    uiState: GigUiState,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (GigCategory) -> Unit,
    onUrgencySelect: (UrgencyLevel?) -> Unit,
    onSortSelect: (GigSort) -> Unit,
    onGigClick: (GigEntity) -> Unit,
    onApplyClick: (GigEntity) -> Unit,
    onBookmarkToggle: (GigEntity) -> Unit,
    onNavigateToPostGig: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Header / Search Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // App Banner & Availability Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(GigPrimary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "QuickGig Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Immediate On-Demand Work",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Available Gigs Counter Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GigAccentContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${uiState.totalGigsCount}+ Available",
                            color = GigOnAccentContainer,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_jobs_input"),
                placeholder = {
                    Text(
                        text = "Search 35+ micro jobs, tasks, locations...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = GigPrimary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GigPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal scroll list
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GigCategory.values().forEach { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GigPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("chip_category_${category.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter row: Urgency Filters & Sort Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Urgency quick toggle
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val isUrgentToday = uiState.selectedUrgency == UrgencyLevel.URGENT_TODAY
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUrgentToday) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable {
                                onUrgencySelect(if (isUrgentToday) null else UrgencyLevel.URGENT_TODAY)
                            }
                            .testTag("filter_urgent_today")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = if (isUrgentToday) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Starts Today",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgentToday) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val isTomorrow = uiState.selectedUrgency == UrgencyLevel.TOMORROW
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isTomorrow) GigAccentContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable {
                                onUrgencySelect(if (isTomorrow) null else UrgencyLevel.TOMORROW)
                            }
                            .testTag("filter_tomorrow")
                    ) {
                        Text(
                            text = "Tomorrow",
                            fontSize = 11.sp,
                            fontWeight = if (isTomorrow) FontWeight.Bold else FontWeight.Normal,
                            color = if (isTomorrow) GigOnAccentContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Sort Dropdown
                Box {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { showSortDropdown = true }
                            .testTag("sort_dropdown_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.selectedSort.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSortDropdown,
                        onDismissRequest = { showSortDropdown = false }
                    ) {
                        GigSort.values().forEach { sortOption ->
                            DropdownMenuItem(
                                text = { Text(sortOption.label) },
                                onClick = {
                                    onSortSelect(sortOption)
                                    showSortDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Active filter status summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.filteredGigs.size} micro jobs available",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.searchQuery.isNotBlank() || uiState.selectedCategory != GigCategory.ALL || uiState.selectedUrgency != null) {
                TextButton(
                    onClick = {
                        onSearchChange("")
                        onCategorySelect(GigCategory.ALL)
                        onUrgencySelect(null)
                    },
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "Reset Filters",
                        fontSize = 12.sp,
                        color = GigPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List of Gigs
        if (uiState.filteredGigs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "No micro jobs match your filters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Try clearing search keywords or selecting 'All Gigs'. Businesses are constantly posting new gigs!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("gigs_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.filteredGigs,
                    key = { it.id }
                ) { gig ->
                    GigCard(
                        gig = gig,
                        isApplied = uiState.appliedGigIds.contains(gig.id),
                        onCardClick = { onGigClick(gig) },
                        onApplyClick = { onApplyClick(gig) },
                        onBookmarkClick = { onBookmarkToggle(gig) }
                    )
                }
            }
        }
    }
}
