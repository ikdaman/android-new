package project.side.ui.util

import androidx.navigation.NavController
import project.side.ui.LOGIN_ROUTE

// AppNavController용
fun NavController.navigateIfLoggedIn(
    isLoggedIn: Boolean,
    onLoggedIn: () -> Unit = {}
) {
    if (isLoggedIn) {
        onLoggedIn()
    } else {
        this.navigate(LOGIN_ROUTE) {
            popUpTo(0)
        }
    }
}