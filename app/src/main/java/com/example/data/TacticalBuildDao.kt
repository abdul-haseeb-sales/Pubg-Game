package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TacticalBuildDao {
    @Query("SELECT * FROM tactical_builds ORDER BY timestamp DESC")
    fun getAllBuilds(): Flow<List<TacticalBuild>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: TacticalBuild)

    @Delete
    suspend fun deleteBuild(build: TacticalBuild)

    @Query("DELETE FROM tactical_builds WHERE id = :id")
    suspend fun deleteBuildById(id: Int)
}
