package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.util.noEffectClick
import java.time.LocalDate

@Composable
fun CalendarDialog(
    show: Boolean,
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onDateConfirmed: (LocalDate) -> Unit
) {
    if (!show) return

    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Close button
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "X",
                        style = DungGeunMoBody,
                        color = TextPrimary,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onDismiss() }
                    )
                }

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
fun CalendarDialogPreview() {
    IkdamanTheme {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CalendarPicker(selectedDate = LocalDate.now())
            }
        }
    }
}
