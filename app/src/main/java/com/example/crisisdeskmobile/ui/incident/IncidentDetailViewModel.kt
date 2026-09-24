package com.example.crisisdeskmobile.ui.incident

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.data.model.TimelineEntry
import com.example.crisisdeskmobile.data.repository.IncidentRepository
import com.example.crisisdeskmobile.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class IncidentDetailViewModel : ViewModel() {
    private val incidentRepository = IncidentRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var currentIncidentId: String = ""

    private val _incidentState = MutableStateFlow<Resource<Incident>>(Resource.Loading)
    val incidentState: StateFlow<Resource<Incident>> = _incidentState.asStateFlow()

    private val _timelineState = MutableStateFlow<Resource<List<TimelineEntry>>>(Resource.Success(emptyList()))
    val timelineState: StateFlow<Resource<List<TimelineEntry>>> = _timelineState.asStateFlow()

    fun initIncident(id: String) {
        if (currentIncidentId == id) return
        currentIncidentId = id
        if (id.isNotBlank()) {
            observeIncident(id)
            observeTimeline(id)
        } else {
            _incidentState.value = Resource.Error("Invalid incident ID")
        }
    }

    private fun observeIncident(id: String) {
        viewModelScope.launch {
            firestore.collection("incidents").document(id)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        _incidentState.value = Resource.Error(error.localizedMessage ?: "Failed to load incident")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val incident = snapshot.toObject(Incident::class.java)?.copy(id = snapshot.id)
                        if (incident != null) {
                            _incidentState.value = Resource.Success(incident)
                        } else {
                            _incidentState.value = Resource.Error("Incident data parse error")
                        }
                    } else {
                        _incidentState.value = Resource.Error("Incident not found")
                    }
                }
        }
    }

    private fun observeTimeline(id: String) {
        viewModelScope.launch {
            firestore.collection("incidents").document(id)
                .collection("timeline")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val entries = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(TimelineEntry::class.java)?.copy(id = doc.id)
                        }
                        _timelineState.value = Resource.Success(entries)
                    }
                }
        }
    }

    fun updateStatus(newStatus: String, resolutionNote: String? = null) {
        if (currentIncidentId.isBlank()) return
        viewModelScope.launch {
            val result = incidentRepository.updateIncidentStatus(currentIncidentId, newStatus, resolutionNote)
            if (result.isSuccess) {
                addTimelineEntry("Status changed to $newStatus${if (resolutionNote != null) " - Note: $resolutionNote" else ""}")
            }
        }
    }

    fun assignToMe() {
        if (currentIncidentId.isBlank()) return
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                firestore.collection("incidents").document(currentIncidentId)
                    .update("assignedTo", currentUser.uid)
                    .await()
                addTimelineEntry("Assigned to operative ${currentUser.email?.substringBefore("@")}")
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun addComment(commentText: String) {
        if (currentIncidentId.isBlank() || commentText.isBlank()) return
        val currentUser = auth.currentUser
        val author = currentUser?.email?.substringBefore("@") ?: "Operative"
        viewModelScope.launch {
            addTimelineEntry("Comment: $commentText", author)
        }
    }

    private suspend fun addTimelineEntry(action: String, authorOverride: String? = null) {
        if (currentIncidentId.isBlank()) return
        val currentUser = auth.currentUser
        val author = authorOverride ?: currentUser?.email?.substringBefore("@") ?: "Operative"
        val entry = TimelineEntry(
            id = firestore.collection("incidents").document(currentIncidentId).collection("timeline").document().id,
            incidentId = currentIncidentId,
            action = action,
            author = author,
            timestamp = System.currentTimeMillis()
        )
        firestore.collection("incidents").document(currentIncidentId)
            .collection("timeline")
            .document(entry.id)
            .set(entry.toMap())
            .await()
    }
}
