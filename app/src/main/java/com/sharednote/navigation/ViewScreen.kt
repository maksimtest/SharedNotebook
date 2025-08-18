package com.sharednote.navigation

import com.sharednote.entity.NoteEntity


data class ViewScreen(
    val typeScreen: TypeScreen,
    val selectedNote: NoteEntity?,
    val readonlyMode: Boolean
)
sealed class TypeScreen(val title: String) {
    object NoteList : TypeScreen("NoteList")
    object NoteItem : TypeScreen("NoteItem")
}