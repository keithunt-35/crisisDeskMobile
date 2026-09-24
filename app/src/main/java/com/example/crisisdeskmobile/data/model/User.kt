package com.example.crisisdeskmobile.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "FieldStaff", // "FieldStaff", "DepartmentLead", "Admin"
    val createdAt: Long = System.currentTimeMillis()
) {
    // Convert to map for Firestore storage in future steps
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "role" to role,
            "createdAt" to createdAt
        )
    }
}
