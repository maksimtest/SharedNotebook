package com.sharednote.data

import androidx.room.withTransaction
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.Boolean

class AppRepository(private val db: AppDatabase) {
    //val dataHelper = DataHelper()

    var activeNotes: Flow<List<NoteEntity>> = db.noteDao().getAllActive()
    suspend fun activeExists(): Boolean = db.noteDao().activeExists()
    var trashNotes: Flow<List<NoteEntity>> = db.noteDao().getAllTrash()

    var folders: Flow<List<FolderEntity>> = db.folderDao().getAll()
    suspend fun countFolders(): Int {
        return db.folderDao().getCount()
    }

    suspend fun renumberFolders(id:Int, num:Int) = db.withTransaction {
        val delta = if(num<0) -3 else 3
        val list = folders.first() // текущее состояние
        val updated = list.mapIndexed { index, folder ->
            if(folder.id ==id){
                folder.copy(num = index*2 + delta)
            } else {
                folder.copy(num = index * 2)
            }
        }
        db.folderDao().updateAll(updated)
    }

    suspend fun updateNote(item: NoteEntity) {
        if (item.id == 0) {
            db.noteDao().insert(item)
        } else {
            db.noteDao().update(item)
        }
    }

    suspend fun insertNote(item: NoteEntity) {
        db.noteDao().insert(item)
    }

    suspend fun updateFolder(item: FolderEntity) {
        if (item.id == 0) {
            db.folderDao().insert(item)
        } else {
            db.folderDao().update(item)
        }
    }

    suspend fun getNoteByTitle(title: String): NoteEntity {
        val listNotes = db.noteDao().getListByTitle(title)
        if (listNotes.isEmpty()) {
            return NoteEntity(0, "newTitle1", "", true, 0)
        }
        return listNotes[0]
    }

    suspend fun deleteFolder(item: FolderEntity) {
        db.folderDao().delete(item)
    }
}
