package com.sharednote.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import com.sharednote.R
import com.sharednote.colors.rememberColorGroups
import com.sharednote.data.MainViewModel
import com.sharednote.colors.ColorGroup
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import com.sharednote.layout.LayoutType
import com.sharednote.layout.SortType
import com.sharednote.layout.ViewSettings
import com.sharednote.settings.LocaleHelper
import com.sharednote.settings.UserSettings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemeColorSettingsDialogColumn(
    mainViewModel: MainViewModel,
    userSettings: UserSettings,
    viewSettings: MutableState<ViewSettings>,
    showSettingsDialog: MutableState<String>
) {
    val colors = mainViewModel.paletteList
    FlowRow(
        modifier = Modifier
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = colorResource(color),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(30.dp))
                    .clickable {
                        userSettings.mainBackground = color
                        viewSettings.value = ViewSettings(
                            viewSettings.value.layoutType,
                            color,
                            viewSettings.value.sortType, 0

                        )
                        showSettingsDialog.value = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                //Text("i=${index + 1}", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FolderSettingsDialogColumn(
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val folders by mainViewModel.folders.collectAsState()
    val allFolderName = context.getString(R.string.ALL_FOLDER)

    val foldersWithAll = remember(folders) {
        (listOf(FolderEntity(0, allFolderName)) + folders).distinctBy { it.id }
    }

    var isSortEditing by rememberSaveable(allFolderName) { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    FlowColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 0.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        foldersWithAll.fastForEachIndexed { index, folder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 0.dp)
                //.border(1.dp, Color.LightGray)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (index == 0) {
                    Text(
                        folder.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text("")
                } else {

                    Row(modifier = Modifier.weight(1f)) {
                        EditableFolderName(folder) { name ->
                            mainViewModel.update(FolderEntity(folder.id, name, folder.num))
                        }
                    }
                    Row() {
                        if (isSortEditing) {
                            IconButton(
                                enabled = index > 1,
                                onClick = {
                                    mainViewModel.renumberAllFolders(folder, -1)
                                }) {
                                if (index < 2) {
                                    Text("")
                                } else {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = "Sorting Up"
                                    )
                                }
                            }
                            IconButton(
                                enabled = index <= foldersWithAll.size - 2,
                                onClick = {
                                    mainViewModel.renumberAllFolders(folder, +1)
                                }) {
                                if (index > foldersWithAll.size - 2) {
                                    Text("")
                                } else {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = "Sorting Down"
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                mainViewModel.delete(folder)
                            }) {
                            Icon(
                                Icons.Default.DeleteForever,
                                contentDescription = "Delete folder"
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 14.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                onClick = {
                    isSortEditing = !isSortEditing
                //mainViewModel.updateIfNoDuplicate(context, FolderEntity(0, "new", 0))
                }) {
                Text("sort", fontSize = 18.sp)
            }
            TextButton(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                onClick = {
                    mainViewModel.updateIfNoDuplicate(context, FolderEntity(0, "new", 0))
                }) {
                Text("+", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun EditableFolderName(
    folder: FolderEntity,
    onSave: (String) -> Unit
) {
    var isEditing by rememberSaveable(folder.id) { mutableStateOf(false) }
    var name by rememberSaveable(folder.id) { mutableStateOf(folder.name) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var hadFocus by remember { mutableStateOf(false) }

    // Фокус и клавиатура — именно при входе в режим редактирования
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    if (isEditing) {
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                isEditing = false
                onSave(name)
                keyboard?.hide()
            }),
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { fs ->
                    if (fs.isFocused) hadFocus = true
                    //Log.d("MyTag", "EditableTitle: onFocusChanged()_2, hadFocus=${hadFocus}, fs.hasFocus=${fs.hasFocus}")
                    // Закрываем редактирование только если фокус уже был и ушёл
                    if (hadFocus && !fs.hasFocus) {
                        isEditing = false
                        hadFocus = false
                        onSave(name)
//                        Log.d("MyTag", "EditableTitle: onFocusChanged()_3" +
//                                ", change isEditing=${isEditing}"+
//                                ", change hadFocus=${hadFocus}")
                    }
                }
                .background(colorResource(R.color.white), RoundedCornerShape(4.dp))
                //.border(0.dp, colorResource(R.color.white), RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp, horizontal = 10.dp),
        )
    } else {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable {
                isEditing = true
                //Log.d("MyTag", "EditableTitle: onClick in Text, change isEditing=true")
            }
        )
    }
}

@Composable
fun TrashSettingsDialogColumn(
    mainViewModel: MainViewModel
) {
    val trashes by mainViewModel.trashedNotes.collectAsState()

    FlowColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        trashes.fastForEachIndexed { index, note ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(note.title, fontSize = 20.sp)

                IconButton(
                    onClick = {
                        mainViewModel.makeActive(note)
                    }) {
                    Icon(Icons.Default.Backup, contentDescription = "Delete folder")
                }

            }
        }

    }
}

@Composable
fun LanguageSettingsDialogColumn(
    userSettings: UserSettings,
    applyLanguage: (String) -> Unit,
    showSettingsDialog: MutableState<String>
) {
    Column {
        Row(
            modifier = Modifier
                .width(160.dp)
                .padding(all = 10.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(0.dp))
                .padding(12.dp)
                .clickable {
                    applyLanguage("en")
                    //LocaleHelper.setAppLanguage("en")
                    //userSettings.language = "en"
                    showSettingsDialog.value = ""
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.usa),
                contentDescription = "USA",
                contentScale = ContentScale.Fit,//.Crop,
                modifier = Modifier
                    .width(30.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text("English", fontSize = 18.sp)
        }
        Row(
            modifier = Modifier
                .width(160.dp)
                .padding(all = 10.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(0.dp))
                .padding(12.dp)
                .clickable {
                    applyLanguage("uk")
                    //LocaleHelper.setAppLanguage("uk")
                    //userSettings.language = "uk"
                    showSettingsDialog.value = ""
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.uk),
                contentDescription = "Ukraine",
                contentScale = ContentScale.Fit,//.Crop,
                modifier = Modifier
                    .width(30.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text("Ukraine", fontSize = 18.sp)
        }
        Row(
            modifier = Modifier
                .width(160.dp)
                .padding(all = 10.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(0.dp))
                .padding(12.dp)
                .clickable {
                    applyLanguage("ru")
                    //LocaleHelper.setAppLanguage("ru")
                    //userSettings.language = "ru"
                    showSettingsDialog.value = ""
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.ru),
                contentDescription = "Russian",
                contentScale = ContentScale.Fit,//.Crop,
                modifier = Modifier
                    .width(30.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text("Russian", fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LayoutTypeSettingsDialogColumn(
    userSettings: UserSettings,
    viewSettings: MutableState<ViewSettings>,
    showSettingsDialog: MutableState<String>
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                //.width(60.dp)
                //.padding(all=10.dp)
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
                .clickable {
                    userSettings.layoutType = LayoutType.LIST.name
                    viewSettings.value = ViewSettings(
                        LayoutType.LIST,
                        viewSettings.value.mainBackgroundRes,
                        viewSettings.value.sortType,
                        viewSettings.value.folderFilterId

                    )
                    showSettingsDialog.value = ""
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.list_view),
                contentDescription = LayoutType.LIST.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(all = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
                .clickable {
                    userSettings.layoutType = LayoutType.GRID.name
                    viewSettings.value = ViewSettings(
                        LayoutType.GRID,
                        viewSettings.value.mainBackgroundRes,
                        viewSettings.value.sortType, viewSettings.value.folderFilterId

                    )
                    showSettingsDialog.value = ""
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_view),
                contentDescription = LayoutType.GRID.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(all = 16.dp)
            )
        }

    }
}

@Composable
fun SortSettingsDialogColumn(
    mainViewModel: MainViewModel,
    userSettings: UserSettings,
    viewSettings: MutableState<ViewSettings>,
    showSettingsDialog: MutableState<String>
) {
    val cases = listOf(
        SortType.NAME_ASC,
        SortType.NAME_DESC,
        SortType.ID_ASC,
        SortType.ID_DESC,
    )
    val selectedColor = colorResource(R.color.folder_bg_active)
    val otherColor = Color.Transparent

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        //verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cases.forEachIndexed { index, case ->
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .padding(8.dp)
                    .background(
                        color = if (viewSettings.value.sortType == case) selectedColor else otherColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .clickable {
                        viewSettings.value = ViewSettings(
                            viewSettings.value.layoutType,
                            viewSettings.value.mainBackgroundRes,
                            case,
                            viewSettings.value.folderFilterId

                        )
                        showSettingsDialog.value = ""
                    },
            ) {
                Text(case.title, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ChangeColorSettingsThemeDialog(
    mainViewModel: MainViewModel,
    noteState: MutableState<NoteEntity>,
    onDismiss: () -> Unit,
    onClick: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnClickOutside = true,           // по умолчанию true, но укажем явно
            dismissOnBackPress = true               // можно выключить, если нужно
        ),
        title = { Text("Choose color/theme") },
        text = {
            ChangeColorThemeSettingsDialogContent() { colorGroupIndex, colorGroup ->
                noteState.value = noteState.value.copy( themeIndex = colorGroupIndex)
                mainViewModel.update(noteState.value)
                onDismiss()
                onClick(colorGroupIndex)
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun ChangeColorThemeSettingsDialogContent(
    onImageClick: (Int, ColorGroup) -> Unit
) {
    val cases = rememberColorGroups()

    FlowRow(
        modifier = Modifier
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cases.forEachIndexed { index, case ->

            Image(
                painter = painterResource(case.img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onImageClick(index, case) }
            )
        }
    }
}

//////////////////////
@Composable
fun HelpSettingsDialogColumn(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val TITLE1 = context.getString(R.string.HELP_DIALOG_TITLE1)
    val TEXT1 = context.getString(R.string.HELP_DIALOG_TEXT1)
    val TITLE2 = context.getString(R.string.HELP_DIALOG_TITLE2)
    val TEXT2 = context.getString(R.string.HELP_DIALOG_TEXT2)

    val TITLE3 = context.getString(R.string.HELP_DIALOG_TITLE3)
    val SUBTITLE3 = context.getString(R.string.HELP_DIALOG_SUBTITLE3)
    val TEXT3 = context.getString(R.string.HELP_DIALOG_TEXT3)

    val SUBTITLE4 = context.getString(R.string.HELP_DIALOG_SUBTITLE4)
    val TEXT4 = context.getString(R.string.HELP_DIALOG_TEXT4)
    val SUBTITLE5 = context.getString(R.string.HELP_DIALOG_SUBTITLE5)
    val TEXT5 = context.getString(R.string.HELP_DIALOG_TEXT5)
    val SUBTITLE6 = context.getString(R.string.HELP_DIALOG_SUBTITLE6)
    val TEXT6 = context.getString(R.string.HELP_DIALOG_TEXT6)
    val SUBTITLE7 = context.getString(R.string.HELP_DIALOG_SUBTITLE7)
    val TEXT7 = context.getString(R.string.HELP_DIALOG_TEXT7)
    val SUBTITLE8 = context.getString(R.string.HELP_DIALOG_SUBTITLE8)
    val TEXT8 = context.getString(R.string.HELP_DIALOG_TEXT8)
    val TITLE9 = context.getString(R.string.HELP_DIALOG_TITLE9)
    val TEXT9 = context.getString(R.string.HELP_DIALOG_TEXT9)

    val TITLE10 = context.getString(R.string.HELP_DIALOG_TITLE10)
    val TEXT10 = context.getString(R.string.HELP_DIALOG_TEXT10)
    val TITLE11 = context.getString(R.string.HELP_DIALOG_TITLE11)
    val TEXT11 = context.getString(R.string.HELP_DIALOG_TEXT11)

    // TODO:
    //  1. update help information
    //  2. localize

    LazyColumn(
        modifier = Modifier
//                .padding(innerPadding)
            //.fillMaxSize()
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 2.dp)
    ) {
        item {
            Text(
                text = "📖 ${TITLE1}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = TEXT1,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SectionTitle("✨ $TITLE2")
            SectionText(TEXT2)
        }


        item {
            SectionTitle("📌 $TITLE3")
            SectionSubtitle(SUBTITLE3)
            SectionText(TEXT3)

            SectionSubtitle(SUBTITLE4)
            SectionText(TEXT4)

            SectionSubtitle(SUBTITLE5)
            SectionText(TEXT5)

            SectionSubtitle(SUBTITLE6)
            SectionText(TEXT6)

            SectionSubtitle(SUBTITLE7)
            SectionText(TEXT7)

            SectionSubtitle(SUBTITLE8)
            SectionText(TEXT8)
        }

        item {
            SectionTitle("⚙️ $TITLE9")
            SectionText(TEXT9)
        }

        item {
            SectionTitle("❓ $TITLE10")
            SectionText(TEXT10)
        }

        item {
            SectionTitle("📬 $TITLE11")
            SectionText(TEXT11)
        }
    }
}

@Composable
fun AboutSettingsDialogColumn(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val numVersion = "1.1"
    val aboutText = context.getString(R.string.ABOUT_DIALOG_VERSION, numVersion)
    LazyColumn(
        modifier = Modifier
//                .padding(innerPadding)
            //.fillMaxSize()
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 2.dp)
    ) {
        item {
            SectionTitle("SharedNote - application.")
            SectionTitle(aboutText)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SectionSubtitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
    )
}

@Composable
fun SectionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}


///////////////////
@Composable
fun ConfirmDeleteDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dialogTitle = context.getString(R.string.CONFIRM_DIALOG_TITLE)
    val dialogText = context.getString(R.string.CONFIRM_DELETE_DIALOG_TEXT, title)
    val dialogYesBtn = context.getString(R.string.CONFIRM_BTN_YES)
    val dialogNoBtn = context.getString(R.string.CONFIRM_BTN_NO)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(dialogTitle) },
        text = { Text(dialogText) },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(dialogYesBtn)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(dialogNoBtn)
            }
        }
    )
}