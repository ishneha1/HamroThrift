package com.example.hamrothrift.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImageUtils(private val context: Context, private val activity: Activity) {

    private var imagePickerLauncher: ActivityResultLauncher<String>? = null

    fun registerLaunchers(onImageSelected: (Uri?) -> Unit) {
        imagePickerLauncher = (activity as androidx.activity.ComponentActivity)
            .registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                onImageSelected(uri)
            }
    }

    fun launchImagePicker() {
        imagePickerLauncher?.launch("image/*")
    }
}