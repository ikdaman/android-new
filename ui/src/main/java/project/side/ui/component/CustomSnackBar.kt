package project.side.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.TextWhite
import project.side.ui.theme.ToastBackground

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            containerColor = ToastBackground,
            contentColor = TextWhite,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = data.visuals.message,
                style = DungGeunMoBody,
                color = TextWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
