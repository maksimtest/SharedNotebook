package com.medimom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FolderEntity)

    @Update
    suspend fun update(item: FolderEntity)

    @Delete
    suspend fun delete(item: FolderEntity)


    @Query("""
        SELECT * 
        FROM folders
        ORDER BY num, id
        """)
    fun getAll(): Flow<List<FolderEntity>>

    @Query("""
        SELECT COUNT(*)        
        FROM folders
        """)
    suspend fun getCount(): Int

}
