package com.example.crisisdeskmobile.ui.incident

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.data.repository.IncidentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogIncidentViewModel : ViewModel() {
    private val incidentRepository = IncidentRepository()
    private val auth = FirebaseAuth.getInstance()

    var title = MutableStateFlow("")
    var category = MutableStateFlow("General") // "AV", "Facilities", "Security", "VIP", "Catering", "General"
    var severity = MutableStateFlow("Medium")  // "Critical", "High", "Medium", "Low"
    var description = MutableStateFlow("")
    var location = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    fun logIncident() {
        val titleStr = title.value.trim()
        val descStr = description.value.trim()
        val locStr = location.value.trim()

        if (titleStr.isEmpty()) {
            _errorMessage.value = "Please enter an incident title"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            _errorMessage.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val incident = Incident(
                title = titleStr,
                category = category.value,
                severity = severity.value,
                description = descStr,
                location = locStr,
                reportedBy = currentUser.uid,
                reportedByEmail = currentUser.email ?: "unknown@crisisdesk.com",
                status = "Open",
                timestamp = System.currentTimeMillis()
            )

            val result = incidentRepository.logIncident(incident)
            _isLoading.value = false

            if (result.isSuccess) {
                _isSuccess.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to log incident"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
