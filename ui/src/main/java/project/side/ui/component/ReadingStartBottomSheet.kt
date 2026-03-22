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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextPrimary
import project.side.ui.util.noEffectClick
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
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(0.dp)
    ) {
        ReadingStartBottomSheetContent(onDismiss = onDismiss, onConfirm = onConfirm)
    }
}

@Composable
internal fun ReadingStartBottomSheetContent(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var showCalendar by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")

    // Calendar bottom sheet for date selection
    CalendarBottomSheet(
        show = showCalendar,
        initialDate = startDate,
        onDismiss = { showCalendar = false },
        onDateConfirmed = { date ->
            startDate = date
            showCalendar = false
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "책 시작하기",
                style = DungGeunMoPopupTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            Text("START", style = DungGeunMoBody, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray)
                    .noEffectClick { showCalendar = true }
                    .padding(12.dp)
            ) {
                Text(
                    text = startDate.format(dateFormatter),
                    style = DungGeunMoBody,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(32.dp))

            Text("FINISH", style = DungGeunMoBody, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "읽는 중",
                    style = DungGeunMoBody,
                    color = TextHint
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(BackgroundGray)
                        .noEffectClick { onDismiss() }
                        .padding(vertical = 12.dp),
                ) {
                    Text("NO", style = DungGeunMoBody, color = TextPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(BackgroundGray)
                        .noEffectClick { onConfirm() }
                        .padding(vertical = 12.dp),
                ) {
                    Text("YES", style = DungGeunMoBody, color = TextPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
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
