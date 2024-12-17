package com.gaitmonitoring.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaitmonitoring.domain.entities.Step
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStep(step: Step)

    @Query("SELECT * FROM Step WHERE id = 0")
    fun getStep(): Flow<Step>

    @Query("SELECT count FROM Step WHERE id = 0")
    fun getStepCount(): Flow<Int>

    @Query("UPDATE Step SET count = count + :steps WHERE id = 0")
    suspend fun incrementStepCount(steps: Int)

    @Query("UPDATE Step SET count = 0 WHERE id = 0")
    suspend fun resetStepCount()

}
