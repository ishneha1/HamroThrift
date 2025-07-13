package com.example.hamrothrift.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.model.ProductUploadRequest
import com.example.hamrothrift.repository.UploadRepository
import com.example.hamrothrift.repository.UploadResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadViewModel(private val repository: UploadRepository) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadResult?>(null)
    val uploadState: StateFlow<UploadResult?> = _uploadState.asStateFlow()

    private val _uploadedProduct = MutableStateFlow<ProductModel?>(null)
    val uploadedProduct: StateFlow<ProductModel?> = _uploadedProduct.asStateFlow()

    fun uploadProduct(
        context: Context,
        imageUri: Uri,
        name: String,
        category: String,
        price: String,
        condition: String,
        description: String
    ) {
        val uploadRequest = ProductUploadRequest(
            name = name,
            category = category,
            price = price,
            condition = condition,
            description = description,
            imageUri = imageUri
        )

        viewModelScope.launch {
            repository.uploadProduct(context, uploadRequest).collect { result ->
                _uploadState.value = result
                if (result is UploadResult.Success) {
                    _uploadedProduct.value = result.product
                }
            }
        }
    }

    fun clearUploadState() {
        _uploadState.value = null
        _uploadedProduct.value = null
    }

    fun validateProductData(
        name: String,
        category: String,
        price: String,
        condition: String,
        imageUri: Uri?
    ): ValidationResult {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Product name is required")
        if (category.isBlank()) errors.add("Category is required")
        if (condition.isBlank()) errors.add("Condition is required")
        if (imageUri == null) errors.add("Product image is required")

        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            errors.add("Valid price is required")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}