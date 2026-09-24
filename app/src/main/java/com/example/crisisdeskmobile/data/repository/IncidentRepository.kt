package com.example.crisisdeskmobile.data.repository

import com.example.crisisdeskmobile.data.model.Incident
import com.example.crisisdeskmobile.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class IncidentRepository {
    private val firestore: FirebaseFirestore by lazy {
        val db = FirebaseFirestore.getInstance()
        try {
            db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build()
                )
                .build()
        } catch (e: Exception) {
            // Ignore if already configured
        }
        db
    }

    private val incidentsCollection = firestore.collection("incidents")

    // Real-time listener for all active incidents
    fun getIncidentsFlow(): Flow<Resource<List<Incident>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = incidentsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Failed to fetch incidents"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val incidents = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Incident::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(incidents))
                }
            }

        awaitClose { listener.remove() }
    }

    // Real-time listener for incidents assigned to current user
    fun getMyIncidentsFlow(userId: String): Flow<Resource<List<Incident>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = incidentsCollection
            .whereEqualTo("assignedTo", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Failed to fetch assigned incidents"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val incidents = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Incident::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(incidents))
                }
            }

        awaitClose { listener.remove() }
    }

    // Log a new incident (Under 20 seconds fast logging requirement)
    suspend fun logIncident(incident: Incident): Result<String> {
        return try {
            val docRef = incidentsCollection.document()
            val newIncident = incident.copy(id = docRef.id, timestamp = System.currentTimeMillis())
            docRef.set(newIncident.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update incident status
    suspend fun updateIncidentStatus(incidentId: String, newStatus: String, resolutionNote: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to newStatus
            )
            if (resolutionNote != null) {
                updates["resolutionNote"] = resolutionNote
            }
            incidentsCollection.document(incidentId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
