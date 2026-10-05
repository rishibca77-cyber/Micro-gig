package com.example.data.model

enum class GigCategory(val displayName: String, val iconName: String) {
    ALL("All Gigs", "apps"),
    DELIVERY_MOVING("Delivery & Moving", "local_shipping"),
    HOSPITALITY_EVENTS("Events & Catering", "restaurant"),
    HOME_YARD("Yard & Home Care", "yard"),
    RETAIL_SHIFTS("Store & Shifts", "storefront"),
    CLEANING("Cleaning", "cleaning_services"),
    TECH_QUICK("Tech & Creative", "devices"),
    PET_CARE("Pet Care", "pets")
}

enum class UrgencyLevel(val label: String) {
    URGENT_TODAY("Starts Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEKEND("This Weekend"),
    FLEXIBLE("Flexible Date")
}

enum class ApplicationStatus(val label: String) {
    PENDING("Under Review"),
    ACCEPTED("Hired / Accepted"),
    COMPLETED("Completed")
}
