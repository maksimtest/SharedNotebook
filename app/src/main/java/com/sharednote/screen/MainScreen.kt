package com.sharednote.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharednote.R
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
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    userSettings: UserSettings,
    onLanguagePicked: (String) -> Unit,
) {
    val viewScreenState = remember { mutableStateOf(ViewScreen(TypeScreen.NoteList, null, true)) }

    when (viewScreenState.value.typeScreen) {
        TypeScreen.NoteList -> NotesListScreen(
            mainViewModel, userSettings,
            onLanguagePicked = { tag -> onLanguagePicked(tag) },
            onAddClick = {
                viewScreenState.value = ViewScreen(TypeScreen.NoteItem, null, true)
            },
            onNoteClick = { item ->
                Log.d("MyTag", "NotesListScreen, item=$item")
                viewScreenState.value = ViewScreen(TypeScreen.NoteItem, item, true)
            },
            onShareClick = { item -> {} })

        TypeScreen.NoteItem -> NoteItemScreen(
            mainViewModel,
            viewScreenState,
            onBackClick = { viewScreenState.value = ViewScreen(TypeScreen.NoteList, null, true) },
            onShareClick = {}
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
            title = { Text(header) },
            text = {
                when (showSettingsDialog.value) {
                    "language" -> ColumnLanguageDialog(
                        userSettings,
                        onLanguagePicked,
                        showSettingsDialog
                    )

                    "theme_color" -> ColumnThemeColorDialog(
                        mainViewModel,
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )

                    "folders" -> ColumnFolderDialog(mainViewModel)

                    "mode" -> ColumnLayoutTypeDialog(
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )

                    "sort" -> ColumnSortDialog(
                        mainViewModel,
                        userSettings,
                        viewSettings,
                        showSettingsDialog
                    )
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
    onLanguagePicked: (String) -> Unit,
    onAddClick: () -> Unit,
    onNoteClick: (NoteEntity) -> Unit,
    onShareClick: (NoteEntity) -> Unit
) {
    val context = LocalContext.current

    val colorInt = R.color.palette1
    //    if (userSetting.mainBackground != 0) userSetting.mainBackground else R.color.palette1

    val initViewSettings = ViewSettings(
        userSetting.getLayoutTypeValue(),
        colorInt,
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
    val foldersWithAll = remember(folders1) {
        (listOf(FolderEntity(0, "All", 0)) + folders1).distinctBy { it.id }
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
                Text(
                    stringResource(R.string.menu_title),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
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
                    title = { Text("SharedNotepad") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = viewSettings.value.mainBackgroundRes)
                    ),

                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Поиск */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
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

                FilterRow(foldersWithAll, viewSettings, padding){
                    showSettingsDialog.value = "folders"
                }

                NoteItemsView(
                    padding,
                    notes,
                    viewSettings,
                    onNoteClick,
                    onShareClick
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
    onFolderDialog:()->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 10.dp)
    ) {
        LazyRow() {
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
        IconButton(onClick = {
            onFolderDialog()
        }) {
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
            .padding(vertical = 8.dp, horizontal = 8.dp)
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