package io.zoemeow.subjectnotifier.view.navbar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
fun MainBottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    onClick: (route: NavBarItems) -> Unit
) {
    NavigationBar {
        MainNavBarItemObject.MainBarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    onClick(navItem)
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = navItem.iconId),
                        contentDescription = stringResource(id = navItem.titleByStringId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = navItem.titleByStringId),
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
            )
        }
    }
}