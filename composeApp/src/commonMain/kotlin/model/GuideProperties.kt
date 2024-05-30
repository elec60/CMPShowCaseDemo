package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

sealed interface GuideItem {

}

data class GuideProperties(
    val titleText: String,
    val contentText: String,
    val titleTextColor: Color = Color.White,
    val contentTextColor: Color = Color.LightGray,
    val backgroundColor: Color = Color.Black,
    val titleTextSize: TextUnit = 24.sp,
    val contentTextSize: TextUnit = 16.sp,
    val coordinates: LayoutCoordinates
) : GuideItem {
}

data class BottomNavItem(val text: String, val icon: ImageVector)