package com.sharednote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val active: Boolean,
    val folderId: Int,
    val themeIndex: Int = 0
) {
    val previewText: String get() = content.take(100)
}