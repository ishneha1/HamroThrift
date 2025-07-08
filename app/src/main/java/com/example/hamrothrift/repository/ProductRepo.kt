package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import com.example.hamrothrift.model.ProductModel

interface ProductRepo {
    fun addProduct( model: ProductModel,
                    callback: (Boolean, String) -> Unit
    )

    fun getProductByProductId(
        productId: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    )

    fun getAllProduct(
        callback: (Boolean, String, List<ProductModel?>) -> Unit
    )

    fun removeProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateProductDetails(
        productId: String, data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context,uri: Uri): String?
}