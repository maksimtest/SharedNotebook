package com.sharednote.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.sharednote.R
import com.sharednote.colors.rememberColorGroups
import com.sharednote.data.MainViewModel
import com.sharednote.entity.ColorGroup
import com.sharednote.entity.FolderEntity
import com.sharednote.entity.NoteEntity
import com.sharednote.entity.themeFrom
import com.sharednote.navigation.TypeScreen
import com.sharednote.navigation.ViewScreen
import kotlin.Boolean


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteItemScreen(
    mainViewModel: MainViewModel,
    viewScreenState: MutableState<ViewScreen>,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {}
) {
    val noteEntity =
        viewScreenState.value.selectedNote ?: NoteEntity(
            0,
            "title1",
            "content",
            true,
            0
        )

    val colorGroups = rememberColorGroups()

    var selectedColorGroup by remember(noteEntity.id, colorGroups) {
        mutableStateOf(noteEntity.themeFrom(colorGroups))
    }
    val isChangeThemeDialogVisibleState = remember { mutableStateOf(false) }
    val isEditingState = remember { mutableStateOf(true) }
    val isOpenChooseFolderState = remember(noteEntity.id) { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val editorState = rememberRichTextState()
    val onShareClick: () -> Unit = {}

    Log.d(
        "MyTag",
        "NoteItemScreen().1, noteEntity: title=${noteEntity.title}, themeIndex=${noteEntity.themeIndex}"
    )
    Log.d("MyTag", "NoteItemScreen().2, selectedColorGroup=${selectedColorGroup.titleColor}")

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            // .statusBarsPadding()
            .pointerInput(Unit) {
                isChangeThemeDialogVisibleState.value = false
                isOpenChooseFolderState.value = false
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                AdBannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                    // .height(AdaptiveBannerHeightDp) // можно не задавать, адаптив сам подберёт
                )
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = selectedColorGroup.titleColor,
                        scrolledContainerColor = selectedColorGroup.titleColor
                    ),
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            EditableTitle(noteEntity) { newTitle: String, note: NoteEntity ->
                                val newNote = NoteEntity(
                                    note.id,
                                    newTitle,
                                    note.content,
                                    true,
                                    note.folderId
                                )
                                mainViewModel.update(newNote)
                            }
                            Row() {

                                IconButton(onClick = onShareClick) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }

                                IconButton(onClick = {
                                    isChangeThemeDialogVisibleState.value = true
                                }) {
                                    Icon(Icons.Default.Palette, contentDescription = "Palette")
                                }

                                if (viewScreenState.value.readonlyMode) {
                                    IconButton(onClick = {
                                        viewScreenState.value = ViewScreen(
                                            viewScreenState.value.typeScreen,
                                            viewScreenState.value.selectedNote,
                                            false
                                        )
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                } else {
                                    IconButton(onClick = {
                                        val newNote = NoteEntity(
                                            noteEntity.id,
                                            noteEntity.title,
                                            editorState.toText(),
                                            true,
                                            noteEntity.folderId
                                        )
                                        mainViewModel.update(newNote)

                                        viewScreenState.value = ViewScreen(
                                            viewScreenState.value.typeScreen,
                                            viewScreenState.value.selectedNote,
                                            true
                                        )
                                    }) {
                                        Icon(Icons.Default.Save, contentDescription = "Save")
                                    }
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewScreenState.value = ViewScreen(TypeScreen.NoteList, null, true)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        content = { innerPadding ->
            if (isChangeThemeDialogVisibleState.value) {
                ChangeColorThemeDialog(
                    mainViewModel,
                    noteEntity,
                    onDismiss = {
                        isChangeThemeDialogVisibleState.value = false
                    },
                    onClick = { index ->
                        selectedColorGroup = colorGroups[index]
                    }
                )
            }

            RichTextEditorContainer(
                mainViewModel,
                isOpenChooseFolderState,
                innerPadding,
                noteEntity,
                viewScreenState,
                editorState,
                onSelectFolder = {
                  isOpenChooseFolderState.value = false
                },
                selectedColorGroup = selectedColorGroup
            )
        }
    )
}

@Composable
fun EditableTitle(
    noteEntity: NoteEntity,
    onTitleSave: (String, NoteEntity) -> Unit
) {
    var isEditing by rememberSaveable(noteEntity.id) { mutableStateOf(false) }
    var title by rememberSaveable(noteEntity.id) { mutableStateOf(noteEntity.title) }
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
            value = title,
            onValueChange = { title = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                isEditing = false
                onTitleSave(title, noteEntity)
                keyboard?.hide()
            }),
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { fs ->
                    if (fs.isFocused) hadFocus = true
                    // Закрываем редактирование только если фокус уже был и ушёл
                    if (hadFocus && !fs.hasFocus) {
                        isEditing = false
                        onTitleSave(title, noteEntity)
                    }
                }
                .background(colorResource(R.color.white), RoundedCornerShape(4.dp))
                //.border(0.dp, colorResource(R.color.white), RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp, horizontal = 10.dp),
        )
    } else {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable { isEditing = true }
        )
    }
}

@Composable
  fun EditableFolder(
    mainViewModel: MainViewModel,
    selectedColorGroup: ColorGroup,
    noteEntity: NoteEntity,
    isEditingFolderState: MutableState<Boolean>,
    onSelectFolder:()->Unit,
    onFolderSave: () -> Unit
) {
    var folderName by rememberSaveable(noteEntity.id) { mutableStateOf("") }
    //var isEditing by rememberSaveable(noteEntity.id) { mutableStateOf(false) }
    //val folders = mainViewModel.folders.collectAsState()

    val folders by mainViewModel.folders.collectAsState()
    val foldersWithAll = remember(folders) {
        (listOf(FolderEntity(0, "All", 0)) + folders).distinctBy { it.id }
    }


//    var title by rememberSaveable(noteEntity.id) { mutableStateOf(noteEntity.title) }
//    val focusRequester = remember { FocusRequester() }
//    val keyboard = LocalSoftwareKeyboardController.current
//    var hadFocus by remember { mutableStateOf(false) }

    // Фокус и клавиатура — именно при входе в режим редактирования
    LaunchedEffect(noteEntity.id) {
        folderName =
            mainViewModel.folders.value.find { it.id == noteEntity.folderId }?.name ?: "All"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(selectedColorGroup.bgColorStart)
            .padding(vertical = 10.dp, horizontal = 40.dp)
        //.border(1.dp, colorResource(R.color.grey), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .background(selectedColorGroup.titleColor)
                .border(1.dp, colorResource(R.color.grey), RoundedCornerShape(8.dp))
                .padding(vertical = 6.dp, horizontal = 10.dp)
        ) {
            Text(
                text = folderName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable { isEditingFolderState.value = true },
                //        .border(1.dp, colorResource(R.color.palette1), RoundedCornerShape(10.dp))
                //        .padding(vertical = 6.dp, horizontal = 10.dp)
            )
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "More",
                modifier = Modifier.rotate(-90f)
            )
        }
    }

    if (isEditingFolderState.value) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start=40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .onGloballyPositioned { coordinates ->
//                        textFieldSize = coordinates.size.toSize()
//                    }
            ) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = {
//                    onUpdate(state.copy(isDropdownExpanded = false))
                    },
                    modifier = Modifier
                        .background(selectedColorGroup.titleColor)
//                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                ) {
                    foldersWithAll.forEachIndexed { i, folder ->
                        DropdownMenuItem(
                            text = { Text(folder.name) },
                            onClick = {
                                val newNote = NoteEntity(
                                    noteEntity.id,
                                    noteEntity.title,
                                    noteEntity.content,
                                    true,
                                    folder.id
                                )
                                mainViewModel.update(newNote)
                                folderName = folder.name
                                onSelectFolder()
                            }
                        )
                        if (i < foldersWithAll.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RichTextEditorContainer(
    mainViewModel: MainViewModel,
    isEditingFolderState: MutableState<Boolean>,
    padding: PaddingValues,
    nodeEntity: NoteEntity,
    viewScreenState: MutableState<ViewScreen>,
    editorState: RichTextState,
    selectedColorGroup: ColorGroup,
    onSelectFolder:()->Unit
) {
    LaunchedEffect(nodeEntity.id) {
        editorState.setHtml(nodeEntity.content)
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        EditableFolder(
            mainViewModel,
            selectedColorGroup,
            nodeEntity,
            isEditingFolderState,
            onSelectFolder
        ) {}

        LinedRichEditor(
            state = editorState,
            selectedColorGroup = selectedColorGroup,
            readOnly = viewScreenState.value.readonlyMode,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinedRichEditor(
    state: RichTextState,
    selectedColorGroup: ColorGroup,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    lineStep: Dp = 27.dp,                 // шаг между линиями (подгони под свой шрифт)
    //lineColor: Color = Color(0xFFE6EBF1), // цвет линий
    lineColor: Color = Color(0x4DAFB2B6),
    radius: Dp = 8.dp,
    vPad: Dp = 8.dp,
    hPad: Dp = 12.dp,
) {
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val stepPx = with(density) { lineStep.toPx() }

    val paperGradient = remember(selectedColorGroup.img, selectedColorGroup.titleColor) {
        Brush.linearGradient(//..verticalGradient(
            colors = listOf(
                selectedColorGroup.bgColorStart,
                //Color(0xFFFFFEFA), // светлый верх
                //Color(0xFFF7F2E8)  // чуть теплее низ
                selectedColorGroup.bgColorEnd
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .border(1.dp, Color.LightGray, RoundedCornerShape(radius))
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(paperGradient) // <- градиент
        )
        // 1) Нижний слой — фон с линиями
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = hPad, vertical = vPad)
                .drawBehind {
                    val w = size.width
                    var y =
                        vPad.toPx()                      // начинаем СРАЗУ от верхнего края контента
                    while (y < size.height) {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = strokePx
                        )
                        y += stepPx
                    }
                }
        )

        // 2) Верхний слой — редактор
        RichTextEditor(
            state = state,
            enabled = true,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,       // размер шрифта
                lineHeight = 1.5.em,    // междустрочный интервал (относительно fontSize)
                // при желании:
                // fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(start = hPad, end = hPad, top = 0.dp, bottom = vPad)
            //.background(Color.Transparent) // важно: без заливки
            ,
            colors = RichTextEditorDefaults.richTextEditorColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            )
        )
    }
}


@Composable
fun LinedRichEditorOld(
    state: RichTextState,                  // твой тип состояния из rich text либы
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFE6EBF1),  // цвет линий
) {
    val density = LocalDensity.current
    val textStyle = MaterialTheme.typography.bodyLarge

    // Шаг между линиями ≈ высота строки
    val lineStepPx = with(density) {
        val lh =
            if (textStyle.lineHeight.isSpecified) textStyle.lineHeight else textStyle.fontSize * 1.3f
        lh.toPx().coerceAtLeast(20f)
    }
    val strokePx = with(density) { 1.dp.toPx() }
    val radius = 8.dp
    val vPad = 12.dp
    val hPad = 16.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))          // чтобы линии не выходили за рамку
            .background(Color.White)
            .drawBehind {
                val w = size.width
                var y =
                    with(density) { vPad.toPx() }  // начинаем после верхнего внутреннего отступа
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = strokePx
                    )
                    y += lineStepPx
                }
            }
            .border(1.dp, Color.LightGray, RoundedCornerShape(radius))
            .padding(horizontal = hPad, vertical = vPad)
    ) {
        RichTextEditor(
            state = state,
            enabled = true,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent) // важно: фон редактора прозрачный
        )
    }
}

fun RichTextState.toggleBold() =
    toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))

fun RichTextState.toggleItalic() =
    toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))

fun RichTextState.toggleUnderline() =
    toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))

fun RichTextState.toggleStrikethrough() =
    toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))

// Списки (в 1.x есть готовые методы)
fun RichTextState.toggleBulletList() = toggleUnorderedList()
fun RichTextState.toggleNumberedList() = toggleOrderedList()