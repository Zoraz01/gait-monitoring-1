package com.gaitmonitoring.navigation


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry

val AnimatedContentTransitionScope<NavBackStackEntry>.exitToBottom: ExitTransition
        get() {
            return slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
        }

    val AnimatedContentTransitionScope<NavBackStackEntry>.enterFromBottom: EnterTransition
        get() {
            return slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
        }

