package com.example.alp_visprog.ui.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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

enum class AppView(val title: String, val icon: ImageVector? = null) {
    Home("Home", Icons.Filled.Home),
    Create(title = "Buat", Icons.Filled.Add),
    Profile(title = "Profil", Icons.Filled.Person),
    ShoppingCart(title = "Keranjang", Icons.Filled.ShoppingCart)
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
            Text(text = currentView?.title ?: AppView.Home.title)
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun CustomBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.shadow(8.dp)) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = Color.White,
            contentColor = Color.Gray,
            tonalElevation = 8.dp
        ) {
            val homeSelected = currentDestination?.hierarchy?.any { it.route == AppView.Home.name } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = if (homeSelected) Color(0xFFFF6B35) else Color(0xFF999999),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        "Home",
                        color = if (homeSelected) Color(0xFFFF6B35) else Color(0xFF666666),
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
                    selectedIconColor = Color(0xFFFF6B35),
                    selectedTextColor = Color(0xFFFF6B35),
                    indicatorColor = Color(0xFFFFE5DB),
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
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = if (profileSelected) Color(0xFFFF6B35) else Color(0xFF999999),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        "Profil",
                        color = if (profileSelected) Color(0xFFFF6B35) else Color(0xFF666666),
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
                    selectedIconColor = Color(0xFFFF6B35),
                    selectedTextColor = Color(0xFFFF6B35),
                    indicatorColor = Color(0xFFFFE5DB),
                    unselectedIconColor = Color(0xFF999999),
                    unselectedTextColor = Color(0xFF666666)
                )
            )
        }

        // FAB
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 0.dp)
                .size(64.dp)
                .shadow(12.dp, CircleShape),
            containerColor = Color(0xFF4A5568),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Buat",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
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

    // Check if current screen is auth screen or splash
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
            // Splash Screen - Entry point
            composable("splash") {
                SplashView(
                    onSplashComplete = {
                        navController.navigate("auth_check") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            // Auth Check Screen - Determines if user is logged in (shows loading indicator)
            composable("auth_check") {
                AuthCheckScreen(
                    onAuthenticated = {
                        navController.navigate(AppView.Home.name) {
                            popUpTo("auth_check") { inclusive = true }
                        }
                    },
                    onNotAuthenticated = {
                        navController.navigate("login") {
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
                sheetState = sheetState
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

/**
 * AuthCheckScreen - Checks authentication status and shows loading indicator
 * This is where the "loading page" properly appears - only when checking auth state
 */
@Composable
fun AuthCheckScreen(
    onAuthenticated: () -> Unit,
    onNotAuthenticated: () -> Unit
) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as com.example.alp_visprog.App
    val userRepository = app.container.userRepository

    LaunchedEffect(Unit) {
        // Check authentication status
        val token = userRepository.currentUserToken.first()

        if (token != "Unknown" && token.isNotBlank()) {
            // User is authenticated
            onAuthenticated()
        } else {
            // User needs to login
            onNotAuthenticated()
        }
    }

    // Show loading indicator while checking
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6E3)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFFF9794D),
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