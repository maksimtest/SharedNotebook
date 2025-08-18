package com.sharednote.layout

import androidx.annotation.ColorRes

data class ViewSettings(
    val layoutType: LayoutType,
    @ColorRes val mainBackgroundRes: Int,
    val sortType: SortType,
    val folderFilterId:Int
)