package com.sharednote.entity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorGroup(
    @DrawableRes val img: Int,
    @ColorRes val titleColor: Color,
    @ColorRes val bgColorStart: Color,
    @ColorRes val bgColorEnd: Color
)

fun NoteEntity.themeFrom(groups: List<ColorGroup>): ColorGroup =
    groups.getOrElse(themeIndex) { groups.first() }
