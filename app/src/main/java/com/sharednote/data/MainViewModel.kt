package com.sharednote.data

import android.content.Context
import android.util.Log
import androidx.collection.emptyIntList
import androidx.collection.intIntMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharednote.R
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    lateinit var paletteList:List<Int>
    lateinit var languageList:List<String>

    val activeNotes = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val trashedNotes = repository.trashNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val folders = repository.folders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun update(item: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(item)
        }
    }
    fun update(item: FolderEntity) {
        viewModelScope.launch {
            repository.updateFolder(item)
        }
    }
    fun delete(item: FolderEntity) {
        viewModelScope.launch {
            repository.deleteFolder(item)
        }
    }


    suspend fun init(context: Context) {
        Log.d("MyTag", "mvm: init()")
        if(!repository.activeExists()){
            Log.d("MyTag", "mvm: init(), add Default note")
            val note = NoteEntity(0, "Template", "Example of...", true,0)
            update(note)
        }
        if(repository.countFolders() == 0){
            initFolders()
        }
        initPalette(context)
    }

    fun initPalette(context: Context){
        paletteList = listOf(
            R.color.palette1, R.color.palette2,
            R.color.palette3, R.color.palette4,
            R.color.palette5, R.color.palette6,
            R.color.palette7, R.color.palette8
        )
    }
    fun initFolders(){
        val f2 = FolderEntity(0, "job", 2)
        val f3 = FolderEntity(0, "home", 3)
        update(f2)
        update(f3)
    }
}