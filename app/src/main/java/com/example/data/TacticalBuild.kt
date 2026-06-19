package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tactical_builds")
data class TacticalBuild(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val primaryWeapon: String,
    val primaryScope: String,
    val primaryMuzzle: String,
    val primaryGrip: String,
    val primaryMagazine: String,
    val secondaryWeapon: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
