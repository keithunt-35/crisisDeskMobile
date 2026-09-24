package com.example.crisisdeskmobile.ui.incident

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.data.repository.IncidentRepository
import com.example.crisisdeskmobile.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyIncidentsViewModel : ViewModel() {
    private val incidentRepository = IncidentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _myIncidentsState = MutableStateFlow<Resource<List<Incident>>>(Resource.Loading)
    val myIncidentsState: StateFlow<Resource<List<Incident>>> = _myIncidentsState.asStateFlow()

    init {
        loadMyIncidents()
    }

    fun loadMyIncidents() {
        val currentUserId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            incidentRepository.getMyIncidentsFlow(currentUserId).collect { resource ->
                _myIncidentsState.value = resource
            }
        }
    }
}
