package com.example.alp_visprog.ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.alp_visprog.views.CreateHelpRequestView // Import your new View
import com.example.alp_visprog.views.ExchangeListView
import com.example.alp_visprog.views.HomeView
import kotlinx.coroutines.launch

enum class AppView(val title: String, val icon: ImageVector? = null) {
    Home("Home", Icons.Filled.Home),
    Create(title = "Buat", Icons.Filled.Add),
    Profile(title = "Profil", Icons.Filled.Person),
    ShoppingCart(title = "Keranjang", Icons.Filled.ShoppingCart) // Add this
}

data class BottonNavItem(val view: AppView, val label: String)

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
        })
}

@Composable
fun CustomBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    onFabClick: () -> Unit, // Changed: Now takes a callback instead of navigating directly
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Gray
        ) {
            // Home item
            val homeSelected = currentDestination?.hierarchy?.any { it.route == AppView.Home.name } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = if (homeSelected) Color(0xFFFF6B35) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "Home",
                        color = if (homeSelected) Color(0xFFFF6B35) else Color.Gray
                    )
                },
                selected = homeSelected,
                onClick = {
                    navController.navigate(AppView.Home.name) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Empty space for FAB
            NavigationBarItem(
                icon = { },
                label = { Text("") },
                selected = false,
                onClick = { },
                enabled = false
            )

            // Profile item
            val profileSelected = currentDestination?.hierarchy?.any { it.route == AppView.Profile.name } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = if (profileSelected) Color(0xFFFF6B35) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "Profil",
                        color = if (profileSelected) Color(0xFFFF6B35) else Color.Gray
                    )
                },
                selected = profileSelected,
                onClick = {
                    navController.navigate(AppView.Profile.name) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Centered FAB for "Buat"
        FloatingActionButton(
            onClick = onFabClick, // Trigger the bottom sheet
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 0.dp)
                .size(64.dp),
            containerColor = Color(0xFF4A5568),
            shape = CircleShape
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

@Composable
fun ProfileScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
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

    // --- Bottom Sheet State ---
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Set to false if you want it to open half-way first
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyTopAppBar(
                currentView = currentView,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                currentDestination = currentDestination,
                onFabClick = {
                    // Open the Bottom Sheet when FAB is clicked
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->

        // The Main Navigation Host
        NavHost(
            navController = navController,
            startDestination = AppView.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppView.Home.name) {
                HomeView(navControlIer = navController)
            }

            // Note: We removed the "Create" composable route because it is now a BottomSheet

            composable(AppView.Profile.name) {
                ProfileScreen()
            }

            composable(
                route = "exchange_list/{helpRequestId}",
                arguments = listOf(
                    navArgument("helpRequestId") { type = NavType.IntType }
                )
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
                com.example.alp_visprog.views.ShoppingCartView(
                    onBackClick = { navController.navigateUp() }
                )
            }
        }

        // --- The Bottom Sheet Implementation ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                // The Content of the Sheet
                CreateHelpRequestView(
                    onBackClick = {
                        // Close the sheet smoothly on success/cancel
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