package com.sharednote.colors

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import com.sharednote.R
import com.sharednote.entity.ColorGroup

@Composable
fun rememberColorGroups(): List<ColorGroup> {
    @DrawableRes val img1 = R.drawable.theme01
    val t1 = colorResource(R.color.block_title1)
    val bs1 = colorResource(R.color.block_bg1)
    val be1 = colorResource(R.color.block_gr1)
    val gr1 = ColorGroup(img1, t1, bs1, be1)

    @DrawableRes val img2 = R.drawable.theme02
    val t2 = colorResource(R.color.block_title2)
    val bs2 = colorResource(R.color.block_bg2)
    val be2 = colorResource(R.color.block_gr2)
    val gr2 = ColorGroup(img2, t2, bs2, be2)

    @DrawableRes val img3 = R.drawable.theme03
    val t3 = colorResource(R.color.block_title3)
    val bs3 = colorResource(R.color.block_bg3)
    val be3 = colorResource(R.color.block_gr3)
    val gr3 = ColorGroup(img3, t3, bs3, be3)

    @DrawableRes val img4 = R.drawable.theme04
    val t4 = colorResource(R.color.block_title4)
    val bs4 = colorResource(R.color.block_bg4)
    val be4 = colorResource(R.color.block_gr4)
    val gr4 = ColorGroup(img4, t4, bs4, be4)

    return remember(gr1, gr2, gr3, gr4) { listOf(gr1, gr2, gr3, gr4) }
}
