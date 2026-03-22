@file:OptIn(ExperimentalMaterial3Api::class)

package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.util.noEffectClick
import java.time.LocalDate

@Composable
fun CalendarBottomSheet(
    show: Boolean,
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onDateConfirmed: (LocalDate) -> Unit
) {
    if (!show) return

    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        dragHandle = null,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CalendarPicker(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(BackgroundGray)
                            .noEffectClick { onDismiss() }
                            .padding(vertical = 12.dp)
                    ) {
                        Text("NO", style = DungGeunMoBody, color = TextPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(BackgroundGray)
                            .noEffectClick { onDateConfirmed(selectedDate) }
                            .padding(vertical = 12.dp)
                    ) {
                        Text("YES", style = DungGeunMoBody, color = TextPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarBottomSheetContentPreview() {
    IkdamanTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarPicker(selectedDate = LocalDate.now())
        }
    }
}
