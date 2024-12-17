package com.gaitmonitoring.navigationDrawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gaitmonitoring.navigation.AppDestination
import com.gaitmonitoring.navigationDrawer.uiChilds.DrawerContentsUI
import com.gaitmonitoring.navigationDrawer.uiChilds.MyTopAppBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun MyModalNavDrawer(
    navHostController: NavHostController,
    topAppBarTitle: String = "",
    contents: @Composable (PaddingValues) -> Unit
) {

    val context = LocalContext.current
    val viewModel: NavDrawerViewModel = koinViewModel()

    val myUser by viewModel.myUser.collectAsStateWithLifecycle()
    val drawerItems by viewModel.drawerItems.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val backStack by navHostController.currentBackStackEntryAsState()
    val currentNavigationRoute = remember(backStack) {
        backStack?.destination?.route
    }
    val showTopAppBar by remember(currentNavigationRoute) {
        derivedStateOf {
            currentNavigationRoute?.let {
                AppDestination.showTopAppBar(it)
            } ?: false
        }
    }


    BackHandler(drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    // snack bar ..
    val snackBarHostState = remember { SnackbarHostState() }
    val mySnackBarData by SnackBarStateHolder.snackBarData.collectAsStateWithLifecycle()
    val snackBarContainerColor = SnackBarStateHolder.rememberSnackBarContainerColor(mySnackBarData)

    LaunchedEffect(key1 = mySnackBarData, block = {
        mySnackBarData?.let {
            SnackBarStateHolder.showSnackBar(
                context = context,
                snackBarHostState = snackBarHostState,
                data = it
            )
        }
    })


    ModalNavigationDrawer(
        gesturesEnabled = showTopAppBar,
        drawerContent = {
            DrawerContentsUI(
                user = myUser,
                drawerItems = drawerItems,
                currentNavigationRoute = currentNavigationRoute,
                onProfileClicked = {
                    navHostController.navigate(AppDestination.Profile.route)
                    coroutineScope.launch { drawerState.close() }
                },
                onItemClicked = { item ->
                    item.navigationDestination?.let {
                        navHostController.navigate(it.route)
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        drawerState = drawerState,
        content = {
            Scaffold(
                topBar = {
                    if (showTopAppBar) {
                        MyTopAppBar(
                            title = topAppBarTitle,
                            onMenuClicked = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackBarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = snackBarContainerColor,
                            contentColor = MaterialTheme.colorScheme.contentColorFor(
                                snackBarContainerColor
                            )
                        )
                    }
                }
            ) {
                contents(it)
            }
        }
    )

}