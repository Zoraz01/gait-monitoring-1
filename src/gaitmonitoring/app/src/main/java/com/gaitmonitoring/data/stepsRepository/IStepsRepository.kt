package com.gaitmonitoring.data.stepsRepository

import com.gaitmonitoring.domain.entities.Step
import kotlinx.coroutines.flow.Flow

interface IStepsRepository {

    suspend fun addStep(step: Step)
    fun getStepsCount(): Flow<Int>
    suspend fun incrementStepCount(steps: Int)
    suspend fun resetSteps()
}