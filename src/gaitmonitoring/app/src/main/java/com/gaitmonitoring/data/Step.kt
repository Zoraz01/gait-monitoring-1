package com.gaitmonitoring.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey val id: Int = 0,  // Use a constant ID because there's only one record
    var count: Int = 0            // Stores the cumulative count of steps
)