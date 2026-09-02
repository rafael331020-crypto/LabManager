package com.rafael.labmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val birthDate: String,
    val cpf: String,
    val sex: String,
    val address: String,
    val insurance: String,
    val notes: String,
    val createdAt: String,
    val updatedAt: String,
    val active: Boolean = true
)
