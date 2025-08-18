package com.sharednote.data

import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    //val dataHelper = DataHelper()

    var activeNotes: Flow<List<NoteEntity>> = db.noteDao().getAllActive()
    suspend fun activeExists(): Boolean = db.noteDao().activeExists()
    var trashNotes: Flow<List<NoteEntity>> = db.noteDao().getAllTrash()

    var folders: Flow<List<FolderEntity>> = db.folderDao().getAll()
    suspend fun countFolders(): Int {
        return db.folderDao().getCount()
    }

    suspend fun updateNote(item: NoteEntity) {
        if (item.id == 0) {
            db.noteDao().insert(item)
        } else {
            db.noteDao().update(item)
        }
    }

    suspend fun updateFolder(item: FolderEntity) {
        if (item.id == 0) {
            db.folderDao().insert(item)
        } else {
            db.folderDao().update(item)
        }
    }

    suspend fun deleteFolder(item: FolderEntity) {
        db.folderDao().delete(item)
    }

}
