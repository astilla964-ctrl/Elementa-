package com.example

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.OrientationSetting
import com.example.ui.compounds.CompoundsScreen
import com.example.ui.lab.LabWorkbenchScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.ElementaTheme
import com.example.ui.tools.ToolsScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val orientationSetting by viewModel.orientationSetting.collectAsStateWithLifecycle()

            // Update Activity Orientation dynamically based on user setting
            LaunchedEffect(orientationSetting) {
                requestedOrientation = when (orientationSetting) {
                    OrientationSetting.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    OrientationSetting.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    OrientationSetting.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }

            ElementaTheme(themeMode = themeMode) {
                ElementaApp(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ElementaApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // BackHandler: Navigate back to LAB workbench if on any sub-screen
    BackHandler(enabled = currentScreen != AppScreen.LAB) {
        viewModel.navigateTo(AppScreen.LAB)
    }

    if (isLandscape) {
        // Landscape Mode: Side Navigation Rail + Content Area
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .testTag("landscape_nav_rail"),
                containerColor = MaterialTheme.colorScheme.surface,
                header = {
                    Text(
                        text = "🧪",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))

                NavigationRailItem(
                    selected = currentScreen == AppScreen.LAB,
                    onClick = { viewModel.navigateTo(AppScreen.LAB) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.LAB) Icons.Filled.Science else Icons.Outlined.Science,
                            contentDescription = "Lab Workbench",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Lab", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_lab")
                )

                NavigationRailItem(
                    selected = currentScreen == AppScreen.TOOLS,
                    onClick = { viewModel.navigateTo(AppScreen.TOOLS) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.TOOLS) Icons.Filled.Build else Icons.Outlined.Build,
                            contentDescription = "Apparatus Tools",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Tools", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tools")
                )

                NavigationRailItem(
                    selected = currentScreen == AppScreen.COMPOUNDS,
                    onClick = { viewModel.navigateTo(AppScreen.COMPOUNDS) },
                    icon = {
                        Text(
                            text = "🧬",
                            fontSize = 18.sp
                        )
                    },
                    label = { Text("Pokedex", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_compounds")
                )

                NavigationRailItem(
                    selected = currentScreen == AppScreen.SETTINGS,
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Settings", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_settings")
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues())
            ) {
                ScreenContent(
                    screen = currentScreen,
                    viewModel = viewModel,
                    isLandscape = true
                )
            }
        }
    } else {
        // Portrait Mode: Scaffold with Bottom Navigation Bar
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("portrait_bottom_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.LAB,
                        onClick = { viewModel.navigateTo(AppScreen.LAB) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.LAB) Icons.Filled.Science else Icons.Outlined.Science,
                                contentDescription = "Lab Workbench",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Lab", fontSize = 12.sp) },
                        modifier = Modifier.testTag("nav_lab")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.TOOLS,
                        onClick = { viewModel.navigateTo(AppScreen.TOOLS) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.TOOLS) Icons.Filled.Build else Icons.Outlined.Build,
                                contentDescription = "Tools",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Tools", fontSize = 12.sp) },
                        modifier = Modifier.testTag("nav_tools")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.COMPOUNDS,
                        onClick = { viewModel.navigateTo(AppScreen.COMPOUNDS) },
                        icon = {
                            Text(text = "🧬", fontSize = 20.sp)
                        },
                        label = { Text("Compounds", fontSize = 12.sp) },
                        modifier = Modifier.testTag("nav_compounds")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Settings", fontSize = 12.sp) },
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScreenContent(
                    screen = currentScreen,
                    viewModel = viewModel,
                    isLandscape = false
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    screen: AppScreen,
    viewModel: MainViewModel,
    isLandscape: Boolean
) {
    when (screen) {
        AppScreen.LAB -> LabWorkbenchScreen(viewModel = viewModel, isLandscape = isLandscape)
        AppScreen.TOOLS -> ToolsScreen(viewModel = viewModel, isLandscape = isLandscape)
        AppScreen.COMPOUNDS -> CompoundsScreen(viewModel = viewModel, isLandscape = isLandscape)
        AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel, isLandscape = isLandscape)
    }
}
