package model

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min

data class ScreenSizeInfo(val heightDp: Dp, val widthDp: Dp) {
    val minDim: Dp
        get() = min(heightDp, widthDp)
}

@Composable
expect fun getScreenSizeInfo(): ScreenSizeInfo

