package project.side.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import project.side.ui.theme.IkdamanTheme

@Composable
fun HistoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("히스토리")
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    IkdamanTheme {
        HistoryScreen()
    }
}
