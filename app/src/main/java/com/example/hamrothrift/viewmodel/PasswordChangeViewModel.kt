package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordChangeViewModel(private val repository: PasswordRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _emailSent = MutableStateFlow(false)
    val emailSent: StateFlow<Boolean> = _emailSent

    fun sendPasswordResetEmail(email: String) {
        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            repository.sendPasswordResetEmail(email)
                .onSuccess {
                    _message.value = "Password reset email sent! Please check your inbox."
                    _emailSent.value = true
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _message.value = exception.message ?: "Failed to send reset email"
                    _isLoading.value = false
                }
        }
    }

    fun changePasswordWithAuth(currentPassword: String, newPassword: String) {
        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            repository.reauthenticateUser(currentPassword)
                .onSuccess {
                    repository.updatePassword(newPassword)
                        .onSuccess {
                            _message.value = "Password updated successfully!"
                            _isSuccess.value = true
                            _isLoading.value = false
                        }
                        .onFailure { exception ->
                            _message.value = exception.message ?: "Failed to update password"
                            _isLoading.value = false
                        }
                }
                .onFailure { exception ->
                    _message.value = "Current password is incorrect"
                    _isLoading.value = false
                }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }
}

class PasswordChangeViewModelFactory(private val repository: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordChangeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordChangeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}