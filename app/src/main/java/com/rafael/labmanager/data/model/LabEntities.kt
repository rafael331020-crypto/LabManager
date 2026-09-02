package com.rafael.labmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val exam: String,
    val material: String,
    val priority: String,
    val physician: String,
    val insurance: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "samples")
data class SampleEntity(
    @PrimaryKey val id: String,
    val barcode: String,
    val patientId: String,
    val patientName: String,
    val material: String,
    val collector: String,
    val origin: String,
    val condition: String,
    val status: String,
    val collectedAt: String,
    val updatedAt: String,
    val recollectionRequired: Boolean = false
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val orderId: String,
    val exam: String,
    val value: String,
    val unit: String,
    val referenceRange: String,
    val status: String,
    val professional: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lot: String,
    val expiryDate: String,
    val quantity: Int,
    val minimumQuantity: Int,
    val supplier: String,
    val updatedAt: String
)

@Entity(tableName = "audit_events")
data class AuditEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val timestamp: String,
    val deviceId: String
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val role: String,
    val active: Boolean = true,
    val createdAt: String
)
