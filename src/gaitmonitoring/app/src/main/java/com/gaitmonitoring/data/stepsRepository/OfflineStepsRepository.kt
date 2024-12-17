package com.gaitmonitoring.data.stepsRepository

import com.gaitmonitoring.data.database.StepDao
import com.gaitmonitoring.domain.entities.Step
import kotlinx.coroutines.flow.Flow

class OfflineStepsRepository(private val stepDao: StepDao): IStepsRepository {


    override suspend fun addStep(step: Step) {
        stepDao.updateStep(step)
    }


    override fun getStepsCount(): Flow<Int> {
        return stepDao.getStepCount()
    }


    override suspend fun incrementStepCount(steps: Int) {
        stepDao.incrementStepCount(steps)
    }


    override suspend fun resetSteps() {
        stepDao.resetStepCount()
    }
}
