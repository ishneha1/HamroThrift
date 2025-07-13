package com.example.hamrothrift.repository

import com.example.hamrothrift.model.BillingAddress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface BillingAddressRepository {
    suspend fun getBillingAddress(userId: String): Result<BillingAddress?>
    suspend fun saveBillingAddress(billingAddress: BillingAddress): Result<Boolean>
    suspend fun deleteBillingAddress(userId: String): Result<Boolean>
}

class BillingAddressRepositoryImpl : BillingAddressRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val billingCollection = firestore.collection("billing_addresses")

    override suspend fun getBillingAddress(userId: String): Result<BillingAddress?> {
        return try {
            val document = billingCollection.document(userId).get().await()
            val billingAddress = if (document.exists()) {
                document.toObject(BillingAddress::class.java)
            } else {
                null
            }
            Result.success(billingAddress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveBillingAddress(billingAddress: BillingAddress): Result<Boolean> {
        return try {
            billingCollection.document(billingAddress.userId).set(billingAddress).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBillingAddress(userId: String): Result<Boolean> {
        return try {
            billingCollection.document(userId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}