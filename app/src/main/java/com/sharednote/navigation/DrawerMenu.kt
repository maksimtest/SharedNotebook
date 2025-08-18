package com.sharednote.navigation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.ViewCozy
import androidx.compose.ui.graphics.vector.ImageVector
import com.sharednote.R


class DrawerMenu(val context: Context) {
    val menuItems = listOf(

    DrawerMenuItem("language", R.string.menu_language, Icons.Default.Language),
    DrawerMenuItem("mode",     R.string.menu_mode,     Icons.Default.ViewCozy),
    DrawerMenuItem("theme_color",    R.string.menu_theme_color, Icons.Default.ColorLens),
    DrawerMenuItem("folders",   R.string.menu_folder,   Icons.Default.FolderCopy),
    DrawerMenuItem("trash",    R.string.menu_trash,    Icons.Default.Cabin),
    DrawerMenuItem("sort",     R.string.menu_sort,     Icons.Default.SortByAlpha),
    DrawerMenuItem("help",     R.string.menu_help,     Icons.Default.Support),
    DrawerMenuItem("about",    R.string.menu_about,    Icons.Default.Info)
    )

    fun getMenu(): List<DrawerMenuItem> {
        return menuItems
    }
}

data class DrawerMenuItem(val type: String, @StringRes val titleRes: Int, val icon: ImageVector)

