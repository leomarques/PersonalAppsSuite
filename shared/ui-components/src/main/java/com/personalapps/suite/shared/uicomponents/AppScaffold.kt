package com.personalapps.suite.shared.uicomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

@Composable
fun AppScaffold(
    navItems: List<NavItem>,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            content(Modifier.fillMaxSize())
        }
        if (navItems.isNotEmpty()) {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = item.isSelected,
                        onClick = item.onClick,
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    }
}
