package com.example.crisisdeskmobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.data.repository.IncidentRepository
import com.example.crisisdeskmobile.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val incidentRepository = IncidentRepository()

    private val _incidentsState = MutableStateFlow<Resource<List<Incident>>>(Resource.Loading)
    val incidentsState: StateFlow<Resource<List<Incident>>> = _incidentsState.asStateFlow()

    init {
        loadIncidents()
    }

    fun loadIncidents() {
        viewModelScope.launch {
            incidentRepository.getIncidentsFlow().collect { resource ->
                _incidentsState.value = resource
            }
        }
    }
}
