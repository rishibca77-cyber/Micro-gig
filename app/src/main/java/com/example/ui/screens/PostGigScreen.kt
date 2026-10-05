package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GigCategory
import com.example.data.model.UrgencyLevel
import com.example.ui.theme.GigAccent
import com.example.ui.theme.GigAccentContainer
import com.example.ui.theme.GigPrimary
import com.example.ui.theme.GigPrimaryContainer
import com.example.ui.theme.GigSuccess
import com.example.ui.theme.GigSuccessContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostGigScreen(
    onPostGig: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GigCategory.DELIVERY_MOVING) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    var isHourly by remember { mutableStateOf(true) }
    var payAmountText by remember { mutableStateOf("30") }
    var durationText by remember { mutableStateOf("3 hours (Flexible afternoon)") }

    var selectedUrgency by remember { mutableStateOf(UrgencyLevel.URGENT_TODAY) }
    var locationText by remember { mutableStateOf("Downtown Metro • 1.5 mi away") }
    var spotsCount by remember { mutableIntStateOf(1) }

    var descriptionText by remember { mutableStateOf("") }
    var requirementsText by remember { mutableStateOf("Punctual and reliable|Able to lift 30 lbs|Wear sturdy shoes") }
    var contactPhone by remember { mutableStateOf("(555) 678-9012") }
    var contactEmail by remember { mutableStateOf("hire@quickbiz.example.com") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        GigCategory.values().filter { it != GigCategory.ALL }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(GigPrimaryContainer, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = GigPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Post an On-Demand Gig",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Businesses & Individuals: Hire reliable local helpers fast",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Form fields in scrollable area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Pro Tip Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GigSuccessContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = GigSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gigs posted with 'Starts Today' get applications within an average of 14 minutes!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Section 1: Business & Role Info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Employer & Job Information",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GigPrimary
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business or Poster Name *") },
                        placeholder = { Text("e.g. Grandview Bistro, Urban Moving Co, or Jane D.") },
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, tint = GigPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_business_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Micro Job Title *") },
                        placeholder = { Text("e.g. Urgent Saturday Banquet Server / Furniture Mover") },
                        leadingIcon = {
                            Icon(Icons.Default.Work, contentDescription = null, tint = GigPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_job_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isCategoryDropdownExpanded,
                        onExpandedChange = { isCategoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_category_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        selectedCategory = cat
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Compensation & Schedule
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "2. Compensation & Timing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GigAccent
                    )

                    // Hourly vs Flat Rate Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = isHourly,
                            onClick = { isHourly = true },
                            label = { Text("Hourly Rate ($/hr)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        FilterChip(
                            selected = !isHourly,
                            onClick = { isHourly = false },
                            label = { Text("Flat Rate Total ($)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = payAmountText,
                        onValueChange = { payAmountText = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text(if (isHourly) "Hourly Rate in USD ($)" else "Total Flat Rate in USD ($)") },
                        leadingIcon = {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = GigAccent)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pay_rate"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Estimated Duration / Shift Time *") },
                        placeholder = { Text("e.g. 3 hours, or 4 hours (Tonight 6PM - 10PM)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_duration"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Urgency Level selection
                    Text(
                        text = "Urgency / Start Date:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UrgencyLevel.values().forEach { urgency ->
                            val isSelected = selectedUrgency == urgency
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedUrgency = urgency },
                                label = { Text(urgency.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (urgency == UrgencyLevel.URGENT_TODAY) Color(0xFFDC2626) else GigPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Section 3: Location & Helpers Needed
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "3. Location & Spots Needed",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = { Text("Location / District *") },
                        placeholder = { Text("e.g. Downtown Harbor Plaza • 1.2 mi away") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GigPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_location"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Spots Needed Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Helpers Needed",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Number of open worker spots",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { if (spotsCount > 1) spotsCount-- },
                                modifier = Modifier.testTag("decrease_spots_btn")
                            ) {
                                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$spotsCount",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { if (spotsCount < 10) spotsCount++ },
                                modifier = Modifier.testTag("increase_spots_btn")
                            ) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section 4: Details & Requirements
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "4. Scope, Requirements & Contact",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Task Description & Scope *") },
                        placeholder = { Text("Clearly describe what needs to be done, equipment provided, and payment details...") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_description"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = requirementsText,
                        onValueChange = { requirementsText = it },
                        label = { Text("Key Requirements (Separate by | or comma)") },
                        placeholder = { Text("e.g. Lift 40 lbs|Own shoes|Punctual") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_requirements"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Phone") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Error display if validation fails
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Submit Button
            Button(
                onClick = {
                    if (businessName.isBlank()) {
                        errorMessage = "Please enter your business or employer name."
                        return@Button
                    }
                    if (jobTitle.isBlank()) {
                        errorMessage = "Please provide a descriptive job title."
                        return@Button
                    }
                    val payNum = payAmountText.toDoubleOrNull() ?: 25.0
                    val formattedPay = if (isHourly) "$$payAmountText/hr" else "$$payAmountText flat"
                    val desc = if (descriptionText.isBlank()) {
                        "On-demand help needed for $jobTitle. Reliable and punctual local service providers welcome to apply."
                    } else descriptionText

                    errorMessage = null
                    onPostGig(
                        jobTitle,
                        businessName,
                        selectedCategory.displayName,
                        formattedPay,
                        payNum,
                        durationText,
                        selectedUrgency.name,
                        locationText,
                        spotsCount,
                        desc,
                        requirementsText,
                        contactPhone,
                        contactEmail
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GigPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_post_gig_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publish Micro Job Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
