package com.example.crisisdeskmobile.ui.incident

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.data.repository.IncidentRepository
import com.example.crisisdeskmobile.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AllIncidentsViewModel : ViewModel() {
    private val incidentRepository = IncidentRepository()

    private val _allIncidents = MutableStateFlow<Resource<List<Incident>>>(Resource.Loading)
    
    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedSeverity = MutableStateFlow("All") // "All", "Critical", "High", "Medium", "Low"
    val selectedCategory = MutableStateFlow("All") // "All", "AV", "Facilities", "Security", etc.
    val selectedStatus = MutableStateFlow("Active") // "Active" (excludes resolved), "Resolved", "All"

    val filteredIncidents: StateFlow<Resource<List<Incident>>> = combine(
        _allIncidents,
        searchQuery,
        selectedSeverity,
        selectedCategory,
        selectedStatus
    ) { incidentsResource, query, severity, category, status ->
        if (incidentsResource is Resource.Success) {
            val filtered = incidentsResource.data.filter { incident ->
                val matchesSearch = query.isBlank() ||
                        incident.title.contains(query, ignoreCase = true) ||
                        incident.description.contains(query, ignoreCase = true) ||
                        incident.location.contains(query, ignoreCase = true)

                val matchesSeverity = severity == "All" || incident.severity == severity
                val matchesCategory = category == "All" || incident.category == category
                val matchesStatus = when (status) {
                    "Active" -> incident.status != "Resolved"
                    "Resolved" -> incident.status == "Resolved"
                    else -> true
                }

                matchesSearch && matchesSeverity && matchesCategory && matchesStatus
            }
            Resource.Success(filtered)
        } else {
            incidentsResource
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.Loading
    )

    init {
        loadAllIncidents()
    }

    private fun loadAllIncidents() {
        viewModelScope.launch {
            incidentRepository.getIncidentsFlow().collect { resource ->
                _allIncidents.value = resource
            }
        }
    }
}
