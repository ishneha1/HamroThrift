package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.hamrothrift.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class ProductRepoImpl : ProductRepo {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    val ref: DatabaseReference = database.reference.child("Products")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dbmwufxbn",
            "api_key" to "511933986824672",
            "api_secret" to "C7WUm7KiQWZl7XaR9guDFTW3wU0"
        )
    )

    override fun addProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id= ref.push().key.toString()
        model.productID=id
        ref.child(id).setValue(model)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Products added")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getProductByProductId(
        productId: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    ) {
        ref.child(productId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var products = snapshot.getValue(ProductModel::class.java)
                    if (products != null) {
                        callback(true, "product fetched", products)
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allProducts = mutableListOf<ProductModel>()
                    for (eachData in snapshot.children) {
                        var products = eachData.getValue(ProductModel::class.java)
                        if (products != null) {
                            allProducts.add(products)
                        }
                    }
                    callback(true, "product fetched", allProducts)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun removeProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "products deleted")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateProductDetails(
        productId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Product Edited")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)


                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")


                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }



    // Implement the methods from ProductRepository interface here
    // For example:
    // override fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
    //     // Implementation code
    // }

    // override fun getProductByProductId(productId: String, callback: (Boolean, String, ProductModel?) -> Unit) {
    //     // Implementation code
    // }

    // ... other methods
}