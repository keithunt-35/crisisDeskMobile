package com.example.crisisdeskmobile.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    val currentUser = auth.currentUser

    // Role state for role-aware UI demonstration ("Field Staff", "Department Lead", "Command Admin")
    private val _userRole = MutableStateFlow("Field Staff")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setUserRole(newRole: String) {
        _userRole.value = newRole
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun logout() {
        auth.signOut()
    }
}
