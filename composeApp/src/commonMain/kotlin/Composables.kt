import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.BottomNavItem
import model.GuideProperties
import model.getScreenSizeInfo
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Preview
@Composable
fun MainScreen() {
    MaterialTheme {
        //key is priority
        val targets = remember { mutableStateMapOf<Int, GuideProperties>() }
        var isShowCaseVisible by remember { mutableStateOf(true) }
        Box {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {},
                        backgroundColor = Color.White,
                        elevation = 10.dp,
                        navigationIcon = {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .onGloballyPositioned {
                                            targets[6] = GuideProperties(
                                                titleText = "Menu",
                                                contentText = "You can open menu by clicking here!",
                                                coordinates = it
                                            )
                                        },
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {}
                    ) {
                        Icon(
                            modifier = Modifier.onGloballyPositioned {
                                targets[5] = GuideProperties(
                                    titleText = "Check email",
                                    contentText = "You can click here to email!",
                                    coordinates = it
                                )
                            },
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email"
                        )
                    }
                },
                bottomBar = {
                    BottomNavigation {
                        bottomBarItems.forEachIndexed { index, item ->
                            BottomNavigationItem(
                                selected = false,
                                onClick = {},
                                icon = {
                                    Icon(
                                        modifier = Modifier.onGloballyPositioned {
                                            targets[index] = GuideProperties(
                                                titleText = item.text,
                                                contentText = "You can click here to navigate to ${item.text} screen",
                                                coordinates = it
                                            )
                                        },
                                        imageVector = item.icon,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("ShowCaseView In ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Compose Multiplatform")
                            }
                            append("!")
                        },
                        color = Color.Black,
                        fontSize = 24.sp
                    )
                }
            }
        }

        if (isShowCaseVisible) {
            ShowCaseView(
                targets = targets,
                onFinish = {
                    isShowCaseVisible = false
                }
            )
        }
    }
}

@Composable
fun ShowCaseView(
    targets: SnapshotStateMap<Int, GuideProperties>,
    onFinish: () -> Unit
) {
    val sortedTargets =
        remember(targets.size) { targets.entries.sortedBy { it.key }.map { it.value } }
    if (sortedTargets.isEmpty()) return

    var currentTargetIndex by remember { mutableStateOf(0) }
    val currentTarget = sortedTargets[currentTargetIndex]
    ShowCaseItemContent(
        properties = currentTarget,
        onTargetTapped = {
            val nextIndex = currentTargetIndex + 1
            if (nextIndex == sortedTargets.size) {
                onFinish()
            } else {
                currentTargetIndex = nextIndex
            }
        }
    )

}

const val extraTargetRadius = 80f
@Composable
fun ShowCaseItemContent(
    properties: GuideProperties,
    onTargetTapped: () -> Unit
) {
    val screenMinDim = getScreenSizeInfo().minDim
    val targetCoordinates = properties.coordinates
    val targetBoundsInRoot = targetCoordinates.boundsInRoot()
    val maxDimension = max(targetCoordinates.size.width, targetCoordinates.size.height)
    val targetRadius = maxDimension.toFloat() + extraTargetRadius

    val outerCircleCenter = targetBoundsInRoot.center
    val outerCircleRadius = with(LocalDensity.current) { screenMinDim.toPx() }
    val outerAnimatable = remember { Animatable(0.2f) }
    val animatable = remember { Animatable(0f) }
    LaunchedEffect(properties) {
        outerAnimatable.snapTo(0.2f)
        outerAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    }
    LaunchedEffect(properties) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(properties) {
                    detectTapGestures {
                        if (targetBoundsInRoot.inflate(delta = extraTargetRadius).contains(it)) {
                            onTargetTapped()
                        }
                    }
                }
        ) {

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = properties.backgroundColor,
                    center = outerCircleCenter,
                    radius = outerCircleRadius * outerAnimatable.value,
                    alpha = 0.9f
                )
                drawCircle(
                    color = Color.White,
                    radius = maxDimension * animatable.value * 10f,
                    center = targetBoundsInRoot.center,
                    alpha = 1 - animatable.value
                )
                drawCircle(
                    color = Color.White,
                    radius = targetRadius,
                    center = targetBoundsInRoot.center,
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

        }
        ShowCaseText(
            guideProperties = properties,
            boundsInParent = targetBoundsInRoot,
            targetRadius = targetRadius
        )
    }
}

@Composable
private fun ShowCaseText(
    guideProperties: GuideProperties,
    boundsInParent: Rect,
    targetRadius: Float
) {
    val density = LocalDensity.current
    var textOffsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .offset(x = 16.dp, y = with(density) { textOffsetY.toDp() })
            .onGloballyPositioned {
                val textHeight = it.size.height
                val top = boundsInParent.center.y - targetRadius - textHeight
                textOffsetY = if (top > 0) {
                    top - 80f
                } else {
                    boundsInParent.center.y + targetRadius * 3
                }
            }
    ) {
        Text(
            text = guideProperties.titleText,
            fontSize = guideProperties.titleTextSize,
            fontWeight = FontWeight.Bold,
            color = guideProperties.titleTextColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = guideProperties.contentText,
            fontSize = guideProperties.contentTextSize,
            fontWeight = FontWeight.Normal,
            color = guideProperties.contentTextColor
        )

    }
}

val bottomBarItems = listOf(
    BottomNavItem("Home", Icons.Default.Home),
    BottomNavItem("Search", Icons.Default.Search),
    BottomNavItem("Favorites", Icons.Default.Favorite),
    BottomNavItem("Settings", Icons.Default.Settings),
    BottomNavItem("Profile", Icons.Default.Person)
)
