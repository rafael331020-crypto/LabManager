package com.rafael.labmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafael.labmanager.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    @Query("SELECT * FROM patients WHERE active = 1 ORDER BY name")
    fun observePatients(): Flow<List<PatientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPatient(patient: PatientEntity)

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun findPatient(id: String): PatientEntity?

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String, updatedAt: String)

    @Query("SELECT * FROM samples ORDER BY collectedAt DESC")
    fun observeSamples(): Flow<List<SampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSample(sample: SampleEntity)

    @Query("UPDATE samples SET status = :status, updatedAt = :updatedAt, recollectionRequired = :recollection WHERE id = :id")
    suspend fun updateSampleStatus(id: String, status: String, recollection: Boolean, updatedAt: String)

    @Query("SELECT * FROM results ORDER BY createdAt DESC")
    fun observeResults(): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertResult(result: ResultEntity)

    @Query("UPDATE results SET status = :status, professional = :professional, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateResultStatus(id: String, status: String, professional: String, updatedAt: String)

    @Query("SELECT * FROM stock_items ORDER BY name")
    fun observeStock(): Flow<List<StockItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStock(item: StockItemEntity)

    @Query("SELECT * FROM users WHERE username = :username AND active = 1 LIMIT 1")
    suspend fun findUser(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAudit(event: AuditEventEntity)

    @Query("SELECT * FROM audit_events ORDER BY timestamp DESC LIMIT 500")
    fun observeAudit(): Flow<List<AuditEventEntity>>
}
