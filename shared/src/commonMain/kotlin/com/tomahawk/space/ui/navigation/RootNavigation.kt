package com.tomahawk.space.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tomahawk.space.getPlatform
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.ui.screens.auth.AuthScreen
import com.tomahawk.space.ui.screens.auth.AuthViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryDetailScreen
import com.tomahawk.space.ui.screens.gallery.GalleryDetailViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryScreen
import com.tomahawk.space.ui.screens.gallery.GalleryViewModel
import com.tomahawk.space.ui.screens.main.MainScreen
import com.tomahawk.space.ui.screens.main.MainViewModel
import com.tomahawk.space.ui.screens.settings.ApiSettingsScreen
import com.tomahawk.space.ui.screens.settings.GeneralSettingsScreen
import com.tomahawk.space.ui.screens.settings.SettingsMainScreen
import com.tomahawk.space.ui.screens.settings.SettingsViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.nav_gallery
import tomahawkspace.shared.generated.resources.nav_general
import tomahawkspace.shared.generated.resources.nav_settings
import kotlin.math.abs
import kotlin.math.roundToInt

sealed class Screen(val route: String, val labelRes: StringResource, val icon: ImageVector) {
    object General : Screen("general", Res.string.nav_general, Icons.Default.Home)
    object Gallery : Screen("gallery", Res.string.nav_gallery, Icons.Default.Menu)
    object Settings : Screen("settings", Res.string.nav_settings, Icons.Default.Settings)
}

fun getRouteOrder(route: String?): Int {
    if (route == null) return 0
    return when {
        route == Screen.General.route -> 10
        route == Screen.Gallery.route -> 20
        route.startsWith("gallery_detail") -> 25
        route == Screen.Settings.route -> 30
        route == "settings_general" -> 31
        route == "settings_api" -> 32
        else -> 0
    }
}

@Composable
fun RootNavigation(
    authRepository: AuthRepository,
    apodRepository: ApodRepository,
    galleryRepository: GalleryRepository
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val key = authRepository.getKey()
        startDestination = if (!key.isNullOrBlank()) "main" else "auth"
    }

    val currentStartDest = startDestination ?: return

    NavHost(
        navController = navController,
        startDestination = currentStartDest
    ) {
        composable("auth") {
            val authViewModel = viewModel { AuthViewModel(authRepository) }
            AuthScreen(
                viewModel = authViewModel,
                onAuthorized = {
                    navController.navigate("main") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("main") {
            MainAppNavigation(
                authRepository = authRepository,
                apodRepository = apodRepository,
                galleryRepository = galleryRepository
            )
        }
    }
}

@Composable
fun MainAppNavigation(
    authRepository: AuthRepository,
    apodRepository: ApodRepository,
    galleryRepository: GalleryRepository
) {
    val navController = rememberNavController()
    val isFloatingNavBar by authRepository.isFloatingNavBar.collectAsState(initial = true)
    val isDesktop = remember { getPlatform().name.contains("Java") }

    val items = listOf(
        Screen.General,
        Screen.Gallery,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showNavBar = currentRoute in items.map { it.route }

    val currentScreen = items.find { it.route == currentRoute } ?: Screen.General

    TomahawkMainScaffold(
        isFloatingNavBar = isFloatingNavBar,
        isDesktop = isDesktop,
        showNavBar = showNavBar,
        currentScreen = currentScreen,
        items = items,
        onScreenSelected = { screen ->
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            val slideSpec = tween<IntOffset>(durationMillis = 320, easing = FastOutSlowInEasing)

            NavHost(
                navController = navController,
                startDestination = Screen.General.route,
                enterTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                exitTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                popEnterTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                popExitTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                }
            ) {
                composable(Screen.General.route) {
                    AnimatedCardWrapper {
                        val mainViewModel: MainViewModel = viewModel {
                            MainViewModel(authRepository, apodRepository, galleryRepository)
                        }
                        MainScreen(
                            viewModel = mainViewModel,
                            contentPadding = contentPadding
                        )
                    }
                }
                composable(Screen.Gallery.route) {
                    AnimatedCardWrapper {
                        val galleryViewModel: GalleryViewModel = viewModel {
                            GalleryViewModel(galleryRepository)
                        }
                        GalleryScreen(
                            viewModel = galleryViewModel,
                            onNavigateToDetail = { date ->
                                navController.navigate("gallery_detail/$date")
                            }
                        )
                    }
                }
                composable(
                    route = "gallery_detail/{date}",
                    arguments = listOf(navArgument("date") { type = NavType.StringType })
                ) { backStackEntry ->
                    AnimatedCardWrapper {
                        val date = backStackEntry.savedStateHandle.get<String>("date") ?: ""
                        val detailViewModel: GalleryDetailViewModel = viewModel {
                            GalleryDetailViewModel(date, galleryRepository, apodRepository, authRepository)
                        }
                        GalleryDetailScreen(
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Screen.Settings.route) {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        SettingsMainScreen(
                            viewModel = settingsViewModel,
                            onNavigateToGeneral = {
                                navController.navigate("settings_general")
                            },
                            onNavigateToApi = {
                                navController.navigate("settings_api")
                            }
                        )
                    }
                }
                composable("settings_general") {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        GeneralSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("settings_api") {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        ApiSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TomahawkMainScaffold(
    isFloatingNavBar: Boolean,
    isDesktop: Boolean,
    showNavBar: Boolean,
    currentScreen: Screen,
    items: List<Screen>,
    onScreenSelected: (Screen) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            bottomBar = {
                if (showNavBar && !isFloatingNavBar && !isDesktop) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        items.forEach { screen ->
                            NavigationBarItem(
                                selected = currentScreen == screen,
                                onClick = { onScreenSelected(screen) },
                                icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                                label = { Text(stringResource(screen.labelRes)) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val contentPadding = when {
                showNavBar && isDesktop -> {
                    PaddingValues(
                        start = 88.dp,
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                }
                showNavBar && isFloatingNavBar -> {
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = 104.dp
                    )
                }
                else -> {
                    innerPadding
                }
            }

            content(contentPadding)
        }

        if (showNavBar && isDesktop) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
            ) {
                Spacer(Modifier.height(16.dp))
                items.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onScreenSelected(screen) },
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                        label = { Text(stringResource(screen.labelRes)) }
                    )
                }
            }
        } else if (showNavBar && isFloatingNavBar) {
            FloatingNavigationCapsule(
                currentScreen = currentScreen,
                items = items,
                onScreenSelected = onScreenSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun FloatingNavigationCapsule(
    currentScreen: Screen,
    items: List<Screen>,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    var contentWidthPx by remember { mutableStateOf(0f) }

    val selectedIndex = items.indexOf(currentScreen).coerceAtLeast(0)
    val tabWidthPx = if (items.isNotEmpty() && contentWidthPx > 0f) contentWidthPx / items.size else 0f

    var isPressed by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableStateOf(0f) }
    var dragVelocity by remember { mutableStateOf(0f) }

    val targetCenterX = if (isDragging) {
        dragX.coerceIn(tabWidthPx / 2f, contentWidthPx - tabWidthPx / 2f)
    } else {
        tabWidthPx * selectedIndex + tabWidthPx / 2f
    }

    val animatedPillCenterX by animateFloatAsState(
        targetValue = targetCenterX,
        animationSpec = if (isDragging) {
            snap()
        } else {
            spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
        },
        label = "pillCenterX"
    )

    val containerScale by animateFloatAsState(
        targetValue = if (isPressed) 1.04f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "containerScale"
    )

    val baseScale = 1.28f
    val speedSensitivityThreshold = 5f
    val speedFactor = (dragVelocity / speedSensitivityThreshold).coerceIn(0f, 1f)

    val targetScaleY = if (isPressed) {
        if (isDragging) {
            baseScale - (baseScale - 0.98f) * speedFactor
        } else {
            baseScale
        }
    } else {
        1.0f
    }

    val targetScaleX = if (isPressed) {
        if (isDragging) {
            baseScale + (1.40f - baseScale) * speedFactor
        } else {
            baseScale
        }
    } else {
        1.0f
    }

    val pillScaleY by animateFloatAsState(
        targetValue = targetScaleY,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "pillScaleY"
    )

    val pillScaleX by animateFloatAsState(
        targetValue = targetScaleX,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "pillScaleX"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = containerScale
                scaleY = containerScale
            }
            .height(68.dp)
            .fillMaxWidth(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = if (isPressed) 12.dp else 6.dp,
        shadowElevation = if (isPressed) 18.dp else 12.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    contentWidthPx = layoutCoordinates.size.width.toFloat()
                }
                .pointerInput(items, contentWidthPx) {
                    val slopPx = 10f
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            isPressed = true
                            isDragging = false
                            dragVelocity = 0f

                            val startTouchX = down.position.x
                            dragX = startTouchX
                            val pointerId = down.id
                            var lastX = startTouchX

                            while (true) {
                                val event = awaitPointerEvent()
                                val dragChange = event.changes.firstOrNull { it.id == pointerId }

                                if (dragChange == null || !dragChange.pressed) {
                                    break
                                }

                                val currentX = dragChange.position.x
                                val deltaX = currentX - lastX

                                if (!isDragging && abs(currentX - startTouchX) > slopPx) {
                                    isDragging = true
                                }

                                if (isDragging) {
                                    dragX = currentX
                                    dragVelocity = abs(deltaX)
                                    dragChange.consume()
                                }

                                lastX = currentX
                            }

                            if (isDragging) {
                                if (tabWidthPx > 0f) {
                                    val releasedIndex = (dragX / tabWidthPx).toInt().coerceIn(0, items.size - 1)
                                    onScreenSelected(items[releasedIndex])
                                }
                            }

                            isPressed = false
                            isDragging = false
                            dragVelocity = 0f
                        }
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Liquid Droplet Slider Pill
            if (tabWidthPx > 0f) {
                val density = LocalDensity.current
                val pillWidthDp = with(density) { tabWidthPx.toDp() }
                val pillOffsetX = with(density) { (animatedPillCenterX - tabWidthPx / 2f).toDp() }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(with(density) { pillOffsetX.toPx() }.roundToInt(), 0) }
                        .width(pillWidthDp)
                        .fillMaxHeight(0.82f)
                        .graphicsLayer {
                            scaleX = pillScaleX
                            scaleY = pillScaleY
                        }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }

            // Tab icons & text labels
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentScreen == screen
                    val contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(0.82f)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onScreenSelected(screen)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.labelRes),
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = stringResource(screen.labelRes),
                                style = MaterialTheme.typography.labelSmall,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedContentScope.AnimatedCardWrapper(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}