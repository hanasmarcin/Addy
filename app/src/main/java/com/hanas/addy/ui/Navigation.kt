package com.hanas.addy.ui

interface NavScreen
data class NavAction(
    val targetScreen: NavScreen,
    val closeCurrentActivity: Boolean = false
)

