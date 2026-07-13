package com.personalapps.suite.shared.uicomponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
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
    title: String,
    navItems: List<NavItem>,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    PersonalScaffold(
        title = title,
        onBackClick = onBackClick,
        bottomBar = {
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
        },
        floatingActionButton = floatingActionButton,
        modifier = modifier
    ) { padding ->
        content(Modifier.padding(padding))
    }
}
