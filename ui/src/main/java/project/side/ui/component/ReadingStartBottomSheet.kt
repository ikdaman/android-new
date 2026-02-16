@file:OptIn(ExperimentalMaterial3Api::class)

package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.ui.theme.IkdamanTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReadingStartBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(),
        dragHandle = null,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp)
    ) {
        ReadingStartBottomSheetContent(onConfirm = onConfirm)
    }
}

@Composable
internal fun ReadingStartBottomSheetContent(
    onConfirm: () -> Unit = {}
) {
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "이 책을 시작할께요.",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            Text("독서 시작", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
            Spacer(Modifier.height(8.dp))
            Text(
                text = today.format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            Spacer(Modifier.height(32.dp))

            Text("독서 종료", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
            Spacer(Modifier.height(8.dp))
            Text(
                text = "읽는 중",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black,
                )
            ) {
                Text("저장", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview
@Composable
fun ReadingStartBottomSheetPreview() {
    IkdamanTheme {
        ReadingStartBottomSheetContent()
    }
}
