package com.sharednote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sharednote.dao.FolderDao
import com.sharednote.dao.NoteDao
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity

@Database(entities = [
    NoteEntity::class, FolderEntity::class],
    version = 13,
    exportSchema = true)

abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                                            context.applicationContext,
                                            AppDatabase::class.java,
                                            "sharedNote_db"
                                        ).fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }

    }
}
