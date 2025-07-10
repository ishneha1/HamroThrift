package com.example.hamrothrift.repository

import com.example.hamrothrift.model.SellerModel
import kotlinx.coroutines.flow.Flow

interface SellerRepo {
    suspend fun getTopSellers(): Flow<List<SellerModel>>
}