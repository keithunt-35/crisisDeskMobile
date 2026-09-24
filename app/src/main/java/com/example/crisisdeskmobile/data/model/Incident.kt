package com.example.crisisdeskmobile.data.model

data class Incident(
    val id: String = "",
    val title: String = "",
    val category: String = "General", // "AV", "Facilities", "Security", "VIP", "Catering"
    val severity: String = "Medium",  // "Critical", "High", "Medium", "Low"
    val status: String = "Open",      // "Open", "In Progress", "Resolved"
    val description: String = "",
    val location: String = "",
    val reportedBy: String = "",
    val reportedByEmail: String = "",
    val assignedTo: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val resolutionNote: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "category" to category,
            "severity" to severity,
            "status" to status,
            "description" to description,
            "location" to location,
            "reportedBy" to reportedBy,
            "reportedByEmail" to reportedByEmail,
            "assignedTo" to assignedTo,
            "timestamp" to timestamp,
            "resolutionNote" to resolutionNote
        )
    }
}
