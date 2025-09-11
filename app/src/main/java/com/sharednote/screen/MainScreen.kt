package com.sharednote.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sharednote.R
import com.sharednote.bt.BtShareViewModel
import com.sharednote.bt.DevicePickerDialog
import com.sharednote.colors.rememberColorGroups
import com.sharednote.data.MainViewModel
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import com.sharednote.layout.LayoutType
import com.sharednote.layout.SortType
import com.sharednote.layout.ViewSettings
import com.sharednote.navigation.DrawerMenu
import com.sharednote.navigation.DrawerMenuItem
import com.sharednote.navigation.TypeScreen
import com.sharednote.navigation.ViewScreen
import com.sharednote.settings.UserSettings
import com.sharednote.share.exportAndShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.String


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    userSettings: UserSettings,
    onLanguagePicked: (String) -> Unit,
) {
    val context = LocalContext.current
    val viewScreenState = remember { mutableStateOf(ViewScreen(TypeScreen.NoteList, null, true)) }
    val scope = rememberCoroutineScope()

    var showReceiver by remember { mutableStateOf(false) }
    var showDevicePicker by remember { mutableStateOf(false) }
    val vm: BtShareViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var noteEntityJsonForShare = remember { mutableStateOf("") }
    var noteIdDeleteConfirmOpen by remember { mutableStateOf(0) }

    if (noteIdDeleteConfirmOpen != 0) {
        ConfirmDeleteDialog(
            title = "Note 1",
            onConfirm = {
                mainViewModel.disActiveNoteId(noteIdDeleteConfirmOpen)
                viewScreenState.value = ViewScreen(TypeScreen.NoteList, null, true)

                noteIdDeleteConfirmOpen = 0
            },
            onDismiss = {
                noteIdDeleteConfirmOpen = 0
            }
        )
    }
    if (showReceiver) {
        // 101
        BtReceiverDialog(mainViewModel, onClose = { showReceiver = false })
    }
//    if (noteEntityJsonForShare.value.isNotEmpty()) {
//        BtReceiverScreen(mainViewModel)

//            onSelect = { device ->
//                // готуємо JSON і шлемо через наш транспорт
//                vm.sendTo(device, noteEntityJsonForShare.value)
//                noteEntityJsonForShare.value = ""
//            },
//            onDismiss = { noteEntityJsonForShare.value = "" }
//        )
//    }

    when (viewScreenState.value.typeScreen) {
        TypeScreen.NoteList -> NotesListScreen(
            mainViewModel, userSettings, noteEntityJsonForShare,
            onLanguagePicked = { tag -> onLanguagePicked(tag) },
            onAddClick = { title ->
                scope.launch {
                    mainViewModel.insertNote(
                        NoteEntity(
                            0,
                            title,
                            "",
                            true,
                            0
                        )
                    )
                    val newNote = mainViewModel.getNoteByTitle(title)
                    viewScreenState.value = ViewScreen(TypeScreen.NoteItem, newNote, true)
                }
            },
            onNoteClick = { item ->
                Log.d("MyTag", "NotesListScreen, item=$item")
                viewScreenState.value = ViewScreen(TypeScreen.NoteItem, item, false)
            },
            onShareReceivedClick = {
                Log.d("MyTag", "MainScreen, onShareReceivedClick, showReceiver set true")
                showReceiver = true
            },
        )

        TypeScreen.NoteItem -> NoteItemScreen(
            mainViewModel,
            viewScreenState,
            onBackClick = { viewScreenState.value = ViewScreen(TypeScreen.NoteList, null, true) },
            onShareClick = { item ->
//                val noteJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
//                val payload = noteJson.encodeToString(NoteEntity.serializer(),item.copy(id = 0))
//                noteEntityJsonForShare.value = payload
                CoroutineScope(Dispatchers.IO).launch {
                    exportAndShare(context, item)
                }
            },
            onDeleteClick = { item ->
                Log.d("MyTag", "onDeleteClick, item=${item}")
                noteIdDeleteConfirmOpen = item.id
            }
        )
    }
}

@Composable
fun ShowSettingsDialog(
    mainViewModel: MainViewModel,
    userSettings: UserSettings,
    viewSettings: MutableState<ViewSettings>,
    showSettingsDialog: MutableState<String>,
    onLanguagePicked: (String) -> Unit,
    menu: List<DrawerMenuItem>
) {
    if (showSettingsDialog.value.isNotEmpty()) {
        val menuMatched = menu.find { it.type == showSettingsDialog.value }
        val header =
            if (menuMatched != null) stringResource(id = menuMatched.titleRes) else "Settings"
        Log.d("MyTag", "ShowSettingsDialog(), showSettingsDialog.value=${showSettingsDialog.value}")
        AlertDialog(
            onDismissRequest = { showSettingsDialog.value = "" },
            properties = DialogProperties(
                usePlatformDefaultWidth = showSettingsDialog.value != "help"
            ),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (showSettingsDialog.value == "help" || showSettingsDialog.value == "language") Arrangement.Start
                    else Arrangement.Center
                ) {
                    if (showSettingsDialog.value == "help") {
                        IconButton(
                            modifier = Modifier
                                .padding(start = 0.dp, end = 40.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(0.dp),
                            onClick = {
                                showSettingsDialog.value = ""
                            }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                        }
                    }
                    Text(header)
                }
            },
            text = {
                when (showSettingsDialog.value) {
                    "language" -> LanguageSettingsDialogColumn(
                        userSettings,
                        onLanguagePicked,
                        showSettingsDialog
                    )

                    "theme_color" -> ThemeColorSettingsDialogColumn(
                        mainViewModel,
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )

                    "folders" -> FolderSettingsDialogColumn(mainViewModel)

                    "mode" -> LayoutTypeSettingsDialogColumn(
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )

                    "trash" -> TrashSettingsDialogColumn(
                        mainViewModel
                    )

                    "sort" -> SortSettingsDialogColumn(
                        mainViewModel,
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )

                    "help" -> HelpSettingsDialogColumn() {
                    }

                    "about" -> AboutSettingsDialogColumn() {
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    mainViewModel: MainViewModel,
    userSetting: UserSettings,
    noteEntityJsonForShare: MutableState<String>,
    onLanguagePicked: (String) -> Unit,
    onAddClick: (title: String) -> Unit,
    onNoteClick: (NoteEntity) -> Unit,
    onShareReceivedClick: () -> Unit
) {
    val context = LocalContext.current

    val colorInt = R.color.palette1
    //    if (userSetting.mainBackground != 0) userSetting.mainBackground else R.color.palette1

    val initViewSettings = ViewSettings(
        userSetting.getLayoutTypeValue(),
        userSetting.mainBackground,//colorInt,
        userSetting.getSortTypeValue(),
        0
    )

    Log.d(
        "MyTag", "NotesListScreen: " +
                "layoutType=${initViewSettings.layoutType.name}" +
                ", mainBackground=${initViewSettings.mainBackgroundRes}, " +
                ", sortTYpe=${initViewSettings.sortType}"
    )

    val viewSettings = remember { mutableStateOf(initViewSettings) }
    val menu: List<DrawerMenuItem> = DrawerMenu(context).getMenu()
    val notes by mainViewModel.activeNotes.collectAsState()
    val folders1 by mainViewModel.folders.collectAsState()
    val allFolderName = context.getString(R.string.ALL_FOLDER)
    val foldersWithAll = remember(folders1) {
        (listOf(FolderEntity(0, allFolderName, 0)) + folders1).distinctBy { it.id }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val showSettingsDialog = remember { mutableStateOf("") }

    ShowSettingsDialog(
        mainViewModel,
        userSetting,
        viewSettings,
        showSettingsDialog,
        onLanguagePicked,
        menu
    )


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.menu_title),
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(end = 10.dp),
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
                Divider()
                LazyColumn() {
                    items(menu) { item ->
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = item.icon,
                                            tint = colorResource(id = viewSettings.value.mainBackgroundRes),
                                            contentDescription = stringResource(item.titleRes),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(stringResource(item.titleRes))
                                    }

                                    Text(
                                        "›",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 28.sp,
                                        color = colorResource(id = viewSettings.value.mainBackgroundRes)
                                    )
                                }
                            },
                            selected = false,
                            onClick = { showSettingsDialog.value = item.type }
                        )
                    }
                }
            }
        }
    ) {
        Log.d("MyTag", " before apply ${viewSettings.value.mainBackgroundRes}")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("SharedNotepad-v24") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = viewSettings.value.mainBackgroundRes)
                    ),

                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onShareReceivedClick() }) {
                            Icon(Icons.Default.Downloading, contentDescription = "Download")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val title = "newTitle"
                        if (notes.any { it.title == title }) {
                            val errorMsg =
                                context.getString(R.string.DUPLICATE_FOLDER_NAME_ERROR, title)
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        } else {
                            onAddClick(title)
                        }
                    },
                    containerColor = colorResource(id = viewSettings.value.mainBackgroundRes)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AdBannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                    // .height(AdaptiveBannerHeightDp) // можно не задавать, адаптив сам подберёт
                )

                FilterRow(foldersWithAll, viewSettings, padding) {
                    showSettingsDialog.value = "folders"
                }

                NoteItemsView(
                    padding,
                    notes,
                    viewSettings,
                    onNoteClick,
                    onShareClick = { note ->
//                        val noteJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
//                        val payload = noteJson.encodeToString(NoteEntity.serializer(),note.copy(id = 0))
//                        noteEntityJsonForShare.value = payload
                        CoroutineScope(Dispatchers.IO).launch {
                            exportAndShare(context, note)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterRow(
    folders: List<FolderEntity>,
    viewSettings: MutableState<ViewSettings>,
    padding: PaddingValues,
    onFolderDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier
                .weight(1f)
        ) {
            items(folders) { folder ->
                FolderItem(folder, viewSettings) {
                    viewSettings.value = ViewSettings(
                        viewSettings.value.layoutType,
                        viewSettings.value.mainBackgroundRes,
                        viewSettings.value.sortType,
                        folder.id
                    )
                }
            }
        }
        IconButton(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .width(26.dp),
            onClick = { onFolderDialog() }
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit folder")
        }
    }
}

@Composable
fun FolderItem(
    folder: FolderEntity,
    viewSettings: MutableState<ViewSettings>,
    onClick: () -> Unit
) {
    val activeColorBg = colorResource(id = R.color.folder_bg_active)
    val noActiveColorBg = colorResource(id = R.color.folder_bg_noactive)

    Row(
        modifier = Modifier
            .background(
                if (viewSettings.value.folderFilterId == folder.id) activeColorBg else noActiveColorBg,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = folder.name,
            fontSize = 18.sp
        )
    }
}

@Composable
fun NoteItemsView(
    padding: PaddingValues,
    notes: List<NoteEntity>,
    viewSettings: MutableState<ViewSettings>,
    onNodeClick: (NoteEntity) -> Unit,
    onShareClick: (NoteEntity) -> Unit

) {
    if (viewSettings.value.layoutType == LayoutType.LIST) {
        LazyColumn(
            modifier = Modifier
                //.padding(padding)
                .padding(all = 10.dp)
        ) {
            items(
                notes
                    .filter {
                        viewSettings.value.folderFilterId == 0 ||
                                viewSettings.value.folderFilterId == it.folderId
                    }.sortedWith(
                        if (viewSettings.value.sortType == SortType.NAME_ASC) compareBy { it.title }
                        else if (viewSettings.value.sortType == SortType.NAME_DESC) compareByDescending { it.title }
                        else if (viewSettings.value.sortType == SortType.ID_ASC) compareBy { it.id }
                        else compareByDescending { it.id }
                    )

            ) { note ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            Log.d("MyTag", "List: card.clickable, note=$note")
                            onNodeClick(note)
                        })
                    //.border(1.dp, Color.Green, RoundedCornerShape(0.dp))
                    ,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    NoteItemContent(note, { onShareClick(note) })
                }
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize(),
            //.padding(padding)
            //.border(1.dp, Color.Red)
            // contentPadding = padding,
            //verticalArrangement = Arrangement.spacedBy(10.dp),
            //horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notes.size) { index ->
                val note = notes[index]
                if (viewSettings.value.folderFilterId == 0 ||
                    viewSettings.value.folderFilterId == note.folderId
                ) {
                    Card(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                            .fillMaxWidth() // в LazyVerticalGrid fillMaxWidth — это ширина ячейки
                            // TODO need move method to another place
                            .clickable {
                                onNodeClick(note)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        NoteItemContent(note, { onShareClick(note) })
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItemContent(
    note: NoteEntity,
    onShareClick: () -> Unit
) {
    val colorGroups = rememberColorGroups()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorGroups[note.themeIndex].bgColorStart)
            .padding(vertical = 10.dp, horizontal = 10.dp)
        //.border(1.dp, Color.Red, RoundedCornerShape(0.dp))
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
            //.padding(12.dp)
        ) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            Text(note.previewText, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

        IconButton(onClick = onShareClick) {
            Icon(Icons.Default.Share, contentDescription = "Share")
        }

    }
}