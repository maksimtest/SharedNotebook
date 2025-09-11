package com.sharednote.settings

import android.content.Context
import android.content.SharedPreferences
import com.sharednote.layout.LayoutType
import androidx.core.content.edit
import com.sharednote.R
import com.sharednote.layout.SortType

class UserSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_SORT_TYPE = "sort_type" // (name, id) _ (asc, desc)
        private const val KEY_LAYOUT_TYPE = "layout_type" // 0 list, 1 blocks
        private const val KEY_MAIN_BACKGROUND = "main_background" // 0 list, 1 blocks
    }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit { putString(KEY_LANGUAGE, value) }

    var sortType: String
        get() = prefs.getString(KEY_SORT_TYPE, SortType.ID_ASC.name) ?: SortType.ID_ASC.name
        set(value) = prefs.edit().putString(KEY_SORT_TYPE, value).apply()

     var layoutType: String // LIST, GRID
        get() = prefs.getString(KEY_LAYOUT_TYPE, "LIST") ?: "LIST"
        set(value) = prefs.edit { putString(KEY_LAYOUT_TYPE, value) }

    var mainBackground: Int
        get() = prefs.getInt(KEY_MAIN_BACKGROUND, R.color.palette1)
        set(value) = prefs.edit { putInt(KEY_MAIN_BACKGROUND, value) }
    fun getLayoutTypeValue(): LayoutType{
        if(LayoutType.GRID.name == layoutType) return LayoutType.GRID
        return LayoutType.LIST
    }
    fun getSortTypeValue(): SortType{
        when(sortType){
            SortType.NAME_ASC.name -> return SortType.NAME_ASC
            SortType.NAME_DESC.name -> return SortType.NAME_DESC
            SortType.ID_DESC.name -> return SortType.ID_DESC
        }
        return SortType.ID_ASC
    }

}