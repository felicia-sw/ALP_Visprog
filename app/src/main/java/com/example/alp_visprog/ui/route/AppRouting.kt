package com.example.alp_visprog.ui.route

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.alp_visprog.views.CreateHelpRequestView
import com.example.alp_visprog.views.ExchangeListView
import com.example.alp_visprog.views.HomeView
import com.example.alp_visprog.views.LoginView
import com.example.alp_visprog.views.ProfileView
import com.example.alp_visprog.views.RegisterView
import com.example.alp_visprog.views.ShoppingCartView
import com.example.alp_visprog.views.SplashView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Brand Colors
private val OrangeGradientStart = Color(0xFFF9794D)
private val OrangeGradientEnd = Color(0xFFFFB399)
private val TealAccent = Color(0xFF4ECDC4)

enum class AppView(val title: String, val icon: ImageVector? = null) {
    Home("Home", Icons.Filled.Home),
    Create(title = "Buat", Icons.Filled.Add),
    Profile(title = "Profil", Icons.Filled.Person),
    ShoppingCart(title = "Keranjang", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    currentView: AppView?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = currentView?.title ?: AppView.Home.title,
                color = Color.White
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OrangeGradientStart
        )
    )
}

@Composable
fun CustomBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false
            )
    ) {
        Surface(
            modifier = Modifier.navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.Gray,
                tonalElevation = 0.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val homeSelected = currentDestination?.hierarchy?.any { it.route == AppView.Home.name } == true
                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (homeSelected) {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                OrangeGradientStart.copy(alpha = 0.15f),
                                                OrangeGradientEnd.copy(alpha = 0.1f)
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Transparent)
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = if (homeSelected) OrangeGradientStart else Color(0xFF999999),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            "Home",
                            color = if (homeSelected) OrangeGradientStart else Color(0xFF666666),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = homeSelected,
                    onClick = {
                        navController.navigate(AppView.Home.name) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangeGradientStart,
                        selectedTextColor = OrangeGradientStart,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF999999),
                        unselectedTextColor = Color(0xFF666666)
                    )
                )

                // Spacer for FAB
                NavigationBarItem(
                    icon = { },
                    label = { Text("") },
                    selected = false,
                    onClick = { },
                    enabled = false
                )

                val profileSelected = currentDestination?.hierarchy?.any { it.route == AppView.Profile.name } == true
                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (profileSelected) {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                TealAccent.copy(alpha = 0.15f),
                                                TealAccent.copy(alpha = 0.1f)
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Transparent)
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profil",
                                tint = if (profileSelected) TealAccent else Color(0xFF999999),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            "Profil",
                            color = if (profileSelected) TealAccent else Color(0xFF666666),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = profileSelected,
                    onClick = {
                        navController.navigate(AppView.Profile.name) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealAccent,
                        selectedTextColor = TealAccent,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF999999),
                        unselectedTextColor = Color(0xFF666666)
                    )
                )
            }
        }

        // Enhanced FAB with Gradient
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 0.dp)
                .size(68.dp)
                .shadow(16.dp, CircleShape),
            containerColor = Color.Transparent,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangeGradientStart,
                                OrangeGradientEnd
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Buat",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRouting() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentView = AppView.entries.find { it.name == currentRoute }

    val isAuthScreen = currentRoute == "register" || currentRoute == "login" || currentRoute == "splash" || currentRoute == "auth_check"

    val shouldShowGlobalTopBar = !isAuthScreen &&
            currentRoute != AppView.Home.name &&
            currentRoute != AppView.ShoppingCart.name

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (shouldShowGlobalTopBar) {
                MyTopAppBar(
                    currentView = currentView,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {
            if (!isAuthScreen) {
                CustomBottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    onFabClick = {
                        showBottomSheet = true
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = if (isAuthScreen) Modifier else Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashView(
                    onSplashComplete = {
                        navController.navigate("auth_check") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("auth_check") {
                AuthCheckScreen(
                    onAuthenticated = {
                        navController.navigate("register") {
                            popUpTo("auth_check") { inclusive = true }
                        }
                    },
                    onNotAuthenticated = {
                        navController.navigate("register") {
                            popUpTo("auth_check") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterView(
                    navController = navController
                )
            }

            composable("login") {
                LoginView(
                    navController = navController
                )
            }

            composable(AppView.Home.name) {
                HomeView(navController = navController)
            }

            composable(AppView.Profile.name) {
                ProfileView(navController = navController)
            }

            composable(
                route = "exchange_list/{helpRequestId}",
                arguments = listOf(navArgument("helpRequestId") { type = NavType.IntType })
            ) { backStackEntry ->
                val helpRequestId = backStackEntry.arguments?.getInt("helpRequestId") ?: 0
                ExchangeListView(
                    helpRequestId = helpRequestId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(
                route = "create_exchange/{helpRequestId}",
                arguments = listOf(navArgument("helpRequestId") { type = NavType.IntType })
            ) { backStackEntry ->
                val helpRequestId = backStackEntry.arguments?.getInt("helpRequestId") ?: 0
                com.example.alp_visprog.views.CreateExchangeView(
                    helpRequestId = helpRequestId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(AppView.ShoppingCart.name) {
                ShoppingCartView(
                    onBackClick = { navController.navigateUp() },
                    onItemClick = { helpRequestId ->
                        navController.navigate("create_exchange/$helpRequestId")
                    }
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null
            ) {
                CreateHelpRequestView(
                    onBackClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AuthCheckScreen(
    onAuthenticated: () -> Unit,
    onNotAuthenticated: () -> Unit
) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as com.example.alp_visprog.App
    val userRepository = app.container.userRepository

    LaunchedEffect(Unit) {
        val token = userRepository.currentUserToken.first()

        if (token != "Unknown" && token.isNotBlank()) {
            onAuthenticated()
        } else {
            onNotAuthenticated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OrangeGradientStart.copy(alpha = 0.1f),
                        OrangeGradientEnd.copy(alpha = 0.05f),
                        Color(0xFFFFFBF7)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = OrangeGradientStart,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Memeriksa status login...",
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
    }
}