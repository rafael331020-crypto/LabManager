package com.rafael.labmanager.data

import com.rafael.labmanager.data.local.LabDao
import com.rafael.labmanager.data.model.*
import kotlinx.coroutines.flow.Flow

class LabRepository(private val dao: LabDao) {
    fun patients(): Flow<List<PatientEntity>> = dao.observePatients()
    fun orders(): Flow<List<OrderEntity>> = dao.observeOrders()
    fun samples(): Flow<List<SampleEntity>> = dao.observeSamples()
    fun results(): Flow<List<ResultEntity>> = dao.observeResults()
    fun stock(): Flow<List<StockItemEntity>> = dao.observeStock()
    fun audit(): Flow<List<AuditEventEntity>> = dao.observeAudit()

    suspend fun savePatient(value: PatientEntity) = dao.upsertPatient(value)
    suspend fun saveOrder(value: OrderEntity) = dao.upsertOrder(value)
    suspend fun saveSample(value: SampleEntity) = dao.upsertSample(value)
    suspend fun saveResult(value: ResultEntity) = dao.upsertResult(value)
    suspend fun saveStock(value: StockItemEntity) = dao.upsertStock(value)
    suspend fun saveUser(value: UserEntity) = dao.upsertUser(value)

    suspend fun setOrderStatus(id: String, status: String, timestamp: String) =
        dao.updateOrderStatus(id, status, timestamp)

    suspend fun setSampleStatus(id: String, status: String, recollection: Boolean, timestamp: String) =
        dao.updateSampleStatus(id, status, recollection, timestamp)

    suspend fun setResultStatus(id: String, status: String, professional: String, timestamp: String) =
        dao.updateResultStatus(id, status, professional, timestamp)

    suspend fun audit(event: AuditEventEntity) = dao.addAudit(event)

    suspend fun findPatient(id: String): PatientEntity? = dao.findPatient(id)
    suspend fun findUser(username: String): UserEntity? = dao.findUser(username)
}
