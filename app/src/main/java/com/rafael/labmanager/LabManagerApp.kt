package com.rafael.labmanager

import android.app.Application
import com.rafael.labmanager.data.LabRepository
import com.rafael.labmanager.data.local.LabDatabase

class LabManagerApp : Application() {
    val database: LabDatabase by lazy { LabDatabase.get(this) }
    val repository: LabRepository by lazy { LabRepository(database.labDao()) }
}
