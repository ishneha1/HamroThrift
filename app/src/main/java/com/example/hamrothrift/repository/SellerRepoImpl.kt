package com.example.hamrothrift.repository

import com.example.hamrothrift.model.SellerModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SellerRepoImpl : SellerRepo {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getTopSellers(): Flow<List<SellerModel>> = callbackFlow {
        val snapshotListener = firestore.collection("sellers")
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val sellers = snapshot.documents.mapNotNull { document ->
                        document.toObject(SellerModel::class.java)?.copy(sellerId = document.id)
                    }
                    trySend(sellers)
                }
            }
        awaitClose { snapshotListener.remove() }
    }
}
