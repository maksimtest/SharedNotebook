package com.sharednote.settings

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    fun setAppLanguage(languageTag: String) {
        val locales = LocaleListCompat.forLanguageTags(languageTag)

        for(i in 0..locales.size()-1){
            Log.d("MyTag", "setAppLanguage: locales="+locales.get(i))
        }

        AppCompatDelegate.setApplicationLocales(locales)
    }
}