package com.sharednote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NoteEntity)

    @Update
    suspend fun update(item: NoteEntity)

    @Query("""
        SELECT * 
        FROM notes
        WHERE active = 1
        """)
    fun getAllActive(): Flow<List<NoteEntity>>

    @Query("""
       SELECT CASE WHEN EXISTS (
            SELECT 1
            FROM notes
            WHERE active = 1
        ) THEN 1 ELSE 0 END AS has_active;
        """)
    fun activeExists(): Boolean

    @Query("""
        SELECT * 
        FROM notes
        WHERE active = 0
        ORDER BY id
        """)
    fun getAllTrash(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * 
        FROM notes
        WHERE title = :title
        ORDER BY id DESC
        """)
    suspend fun getListByTitle(title:String): List<NoteEntity>
}
