package com.sharednote.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sharednote.R


@Composable
fun AdBannerView(
    modifier: Modifier = Modifier,
    adUnitId1: String = "ca-app-pub-3940256099942544/6300978111" // Test code!
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
        ,
        factory = { ctx ->
            AdView(ctx).apply {
                // Рассчитываем адаптивный размер под текущую ширину
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    ctx, getAdWidthInDp(ctx)
                )
                setAdSize(adSize)
                adUnitId = adUnitId1
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

// ширина экрана в dp для адаптивного баннера
private fun getAdWidthInDp(context: Context): Int {
    val metrics = context.resources.displayMetrics
    val widthPixels = metrics.widthPixels.toFloat()
    val density = metrics.density
    return (widthPixels / density).toInt()
}
