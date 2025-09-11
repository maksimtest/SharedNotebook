package com.sharednote.settings

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    fun applyLanguage(activity: Activity, languageTag: String, userSettings: UserSettings) {
        if (Build.VERSION.SDK_INT >= 33) {
            setAppLanguageApi33(activity, languageTag)
        } else {
            // Сохрани язык в prefs; attachBaseContext подхватит при пересоздании
        }
        userSettings.language = languageTag
        activity.recreate()
    }

    @RequiresApi(33)
    fun setAppLanguageApi33(context: Context, languageTag: String) {
        val locale = Locale.forLanguageTag(languageTag)
        context.getSystemService(android.app.LocaleManager::class.java)
            .applicationLocales = android.os.LocaleList(locale)
    }

    @Suppress("DEPRECATION")
    fun updateLocalePre33(context: Context, languageTag: String): Context {
        val locale = Locale.forLanguageTag(languageTag)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

}