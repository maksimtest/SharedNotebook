package com.sharednote.share

import android.content.Context
import android.content.Intent
import com.sharednote.entity.NoteEntity

fun exportAndShare(context: Context, note: NoteEntity) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Note")
        putExtra(Intent.EXTRA_TITLE, note.title)
        putExtra(Intent.EXTRA_TEXT, note.content)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share note").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

