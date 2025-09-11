package com.sharednote.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sharednote.bt.DevicePickerDialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharednote.bt.BtShareViewModel
import com.sharednote.data.MainViewModel
import com.sharednote.entity.NoteEntity
import kotlinx.serialization.json.Json

@Composable
fun BtReceiverDialog(
    mainViewModel: MainViewModel,
    onClose: () -> Unit,
    vm: BtShareViewModel = viewModel()
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Повноекранна поверхня
        androidx.compose.material3.Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Прийом через Bluetooth", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onClose) { Text("Закрити") }
                }
                Spacer(Modifier.height(8.dp))
                // ТУТ твій екран-приймач; він сам стартує/зупиняє сервер
                BtReceiverScreen(mainViewModel,vm = vm)
            }
        }
    }
}
@Composable
fun BtReceiverScreen(mainViewModel: MainViewModel,vm: BtShareViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val need12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    var hasConnect by remember {
        mutableStateOf(
            !need12 || ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val askConnect = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasConnect = granted }

    LaunchedEffect(Unit) {
        if (need12 && !hasConnect) {
            askConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    // Старт/стоп сервера
    LaunchedEffect(hasConnect) {
        if (hasConnect) vm.startReceiver(){note->
            mainViewModel.updateAfterShare(note)
        }

    }
    DisposableEffect(Unit) { onDispose { vm.stopReceiver() } }

    // Примітивний UI
    Column {
        if (!hasConnect && need12) {
            Text("Потрібен дозвіл BLUETOOTH_CONNECT")
            Button(onClick = { askConnect.launch(Manifest.permission.BLUETOOTH_CONNECT) }) {
                Text("Надати дозвіл")
            }
        } else {
            Text(if (vm.isReceiving) "Очікую підключення…" else "Не слухаю")
            vm.error?.let { Text("Помилка: $it", color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))
            Text(vm.lastReceivedText ?: "Ще нічого не отримано")
        }
    }
}

@Composable
fun SendNoteBtButton(
    note: NoteEntity,
    vm: BtShareViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showPicker by remember { mutableStateOf(false) }

//    Button(onClick = { showPicker = true }) {
//        Text("Надіслати через Bluetooth")
//    }

    if (showPicker) {
        DevicePickerDialog(
            onSelect = { device ->
                // готуємо JSON і шлемо через наш транспорт
                val NoteJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
                val payload = NoteJson.encodeToString(NoteEntity.serializer(),note.copy(id = 0))
                vm.sendTo(device, payload)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
fun BtSenderScreen(
    vm: BtShareViewModel = viewModel(),
    noteJson: String // сюди передай серіалізовану нотатку
) {
    var showPicker by remember { mutableStateOf(false) }

    Log.d("MyTag", "!!!BtSenderScreen")

    Column(Modifier.padding(16.dp)) {
        Text("Готово до відправки через Bluetooth")
        Spacer(Modifier.height(8.dp))
        if (vm.isSending) Text("Надсилаю…")
        vm.error?.let { Text("Помилка: $it", color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { showPicker = true }, enabled = !vm.isSending) {
            Text("Обрати пристрій і надіслати")
        }
    }

    if (showPicker) {
        Log.d("MyTag", "!!!BtSenderScreen, before DevicePicker")
        DevicePickerDialog(
            onSelect = { device ->
                vm.sendTo(device, noteJson)
            },
            onDismiss = { showPicker = false }
        )
    }
}
