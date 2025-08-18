package com.sharednote

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.sharednote.settings.UserSettings
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.sharednote.data.AppDatabase
import com.sharednote.data.AppRepository
import com.sharednote.data.MainViewModel
import com.sharednote.data.MainViewModelFactory
import com.sharednote.settings.LocaleHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApp : Application(), ViewModelStoreOwner {
    lateinit var userSettings: UserSettings
    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    lateinit var mainViewModel: MainViewModel
        private set

    lateinit var repository: AppRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        userSettings = UserSettings(applicationContext)

        val db = AppDatabase.getInstance(this)
        repository = AppRepository(db)

        val factory = MainViewModelFactory(repository)

        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        applicationScope.launch {
            mainViewModel.init(applicationContext)
        }
    }

}

