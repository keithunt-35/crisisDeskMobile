package com.example.crisisdeskmobile.data.model

data class TimelineEntry(
    val id: String = "",
    val incidentId: String = "",
    val action: String = "", // "Created", "Status Changed", "Assigned", "Comment Added"
    val author: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "incidentId" to incidentId,
            "action" to action,
            "author" to author,
            "timestamp" to timestamp
        )
    }
}
