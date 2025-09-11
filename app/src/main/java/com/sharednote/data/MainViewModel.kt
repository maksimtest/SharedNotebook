package com.sharednote.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharednote.R
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.String

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    lateinit var paletteList: List<Int>
    lateinit var languageList: List<String>

    val activeNotes = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val trashedNotes = repository.trashNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val folders = repository.folders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun update(item: NoteEntity) {
        Log.d("MyTag", "MVVM: update item=${item}")
        viewModelScope.launch {
            repository.updateNote(item)
        }
    }
    fun updateAfterShare(item: NoteEntity) {
        Log.d("MyTag", "MVVM: updateAfterShare item=${item}")
        viewModelScope.launch {
            repository.updateNote(item)
        }
    }

    fun renumberAllFolders(folder: FolderEntity, num:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.renumberFolders(folder.id, num)
        }
    }
    suspend fun insertNote(item: NoteEntity) {
        repository.insertNote(item)
    }

    suspend fun getNoteByTitle(title: String): NoteEntity {
        return repository.getNoteByTitle(title)
    }

    fun update(item: FolderEntity) {
        viewModelScope.launch {
            repository.updateFolder(item)
        }
    }
    fun updateIfNoDuplicate(context:Context, item: FolderEntity) {
        val msgError = context.getString(R.string.DUPLICATE_FOLDER_NAME_ERROR, item.name)
        viewModelScope.launch {
            if(folders.value.find{it.name == item.name} == null) {
                repository.updateFolder(item)
            } else {
                Toast.makeText(context, msgError, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun delete(item: FolderEntity) {
        viewModelScope.launch {
            repository.deleteFolder(item)
        }
    }

    fun makeActive(item: NoteEntity) {
        val newNote =
            NoteEntity(
                item.id,
                item.title,
                item.content,
                true,
                item.folderId,
                item.themeIndex)
        viewModelScope.launch {
            repository.updateNote(newNote)
        }
    }

    fun disActiveNoteId(itemId: Int) {
        viewModelScope.launch {
            val noteMath = activeNotes.value.find { it.id==itemId }
            if(noteMath != null) {
                val newNote = NoteEntity(
                    noteMath.id,
                    noteMath.title,
                    noteMath.content,
                    false,
                    noteMath.folderId,
                    noteMath.themeIndex
                )
                Log.d("MyTag", "disActiveNote: newNote=${newNote}")
                repository.updateNote(newNote)
            }
        }
    }


    suspend fun init(context: Context) {
        Log.d("MyTag", "mvm: init()")
        if (!repository.activeExists()) {
            Log.d("MyTag", "mvm: init(), add Default note")
            val note = NoteEntity(0, "Template", "Example of...", true, 0)
            update(note)
        }
        if (repository.countFolders() == 0) {
            initFolders()
        }
        initPalette(context)
    }

    fun initPalette(context: Context) {
        paletteList = listOf(
            R.color.palette1, R.color.palette2,
            R.color.palette3, R.color.palette4,
            R.color.palette5, R.color.palette6,
            R.color.palette7, R.color.palette8
        )
    }

    fun initFolders() {
        val f2 = FolderEntity(0, "job", 2)
        val f3 = FolderEntity(0, "home", 3)
        update(f2)
        update(f3)
    }
}