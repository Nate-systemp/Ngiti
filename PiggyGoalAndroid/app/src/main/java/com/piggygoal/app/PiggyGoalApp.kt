package com.piggygoal.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.piggygoal.app.di.AppContainer
import com.piggygoal.app.ui.navigation.PiggyGoalDestination
import com.piggygoal.app.ui.screen.GoalDetailScreen
import com.piggygoal.app.ui.screen.GoalEditorScreen
import com.piggygoal.app.ui.screen.HomeScreen
import com.piggygoal.app.ui.screen.SettingsScreen
import com.piggygoal.app.ui.theme.PiggyGoalTheme
import com.piggygoal.app.ui.viewmodel.GoalDetailViewModelFactory
import com.piggygoal.app.ui.viewmodel.GoalEditorViewModelFactory
import com.piggygoal.app.ui.viewmodel.HomeViewModelFactory
import com.piggygoal.app.ui.viewmodel.SettingsViewModel
import com.piggygoal.app.ui.viewmodel.SettingsViewModelFactory

@Composable
fun PiggyGoalApp() {
    val container = LocalContext.current.appContainer()
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(container.settingsRepository),
    )
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    PiggyGoalTheme(themeMode = settingsState.themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val topLevelRoutes = setOf(PiggyGoalDestination.Home.route, PiggyGoalDestination.Settings.route)
        val showBottomBar = currentDestination?.route in topLevelRoutes
        val showFab = currentDestination?.route == PiggyGoalDestination.Home.route

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        listOf(
                            PiggyGoalDestination.Home to Icons.Rounded.Home,
                            PiggyGoalDestination.Settings to Icons.Rounded.Settings,
                        ).forEach { (destination, icon) ->
                            val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = destination.route) },
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (showFab) {
                    FloatingActionButton(
                        onClick = { navController.navigate(PiggyGoalDestination.GoalEditor.createRoute()) },
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add goal")
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = PiggyGoalDestination.Home.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(PiggyGoalDestination.Home.route) {
                    val viewModel = viewModel<com.piggygoal.app.ui.viewmodel.HomeViewModel>(
                        factory = HomeViewModelFactory(container.repository),
                    )
                    HomeScreen(
                        viewModel = viewModel,
                        onAddGoal = { navController.navigate(PiggyGoalDestination.GoalEditor.createRoute()) },
                        onGoalSelected = { goalId -> navController.navigate(PiggyGoalDestination.GoalDetail.createRoute(goalId)) },
                    )
                }
                composable(PiggyGoalDestination.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
                composable(PiggyGoalDestination.GoalEditor.route) { backStackEntry ->
                    val goalId = backStackEntry.arguments?.getString("goalId")?.toLongOrNull()?.takeIf { it > 0 }
                    val viewModel = viewModel<com.piggygoal.app.ui.viewmodel.GoalEditorViewModel>(
                        factory = GoalEditorViewModelFactory(goalId, container.repository, container.settingsRepository),
                    )
                    GoalEditorScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable(PiggyGoalDestination.GoalDetail.route) { backStackEntry ->
                    val goalId = backStackEntry.arguments?.getString("goalId")?.toLongOrNull() ?: return@composable
                    val viewModel = viewModel<com.piggygoal.app.ui.viewmodel.GoalDetailViewModel>(
                        factory = GoalDetailViewModelFactory(goalId, container.repository),
                    )
                    GoalDetailScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(PiggyGoalDestination.GoalEditor.createRoute(goalId)) },
                        onDeleted = {
                            navController.popBackStack(PiggyGoalDestination.Home.route, false)
                        },
                    )
                }
            }
        }
    }
}

private fun android.content.Context.appContainer(): AppContainer =
    (applicationContext as PiggyGoalApplication).appContainer
