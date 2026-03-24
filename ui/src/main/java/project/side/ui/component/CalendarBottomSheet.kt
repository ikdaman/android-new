package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.util.noEffectClick
import java.time.LocalDate

@Composable
fun CalendarBottomSheet(
    show: Boolean,
    initialDate: LocalDate = LocalDate.now(),
    allowDeselect: Boolean = false,
    onDismiss: () -> Unit,
    onDateConfirmed: (LocalDate?) -> Unit
) {
    if (!show) return

    var selectedDate by remember(initialDate) { mutableStateOf<LocalDate?>(initialDate) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PixelShadowBox(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            backgroundColor = BackgroundWhite,
            shadowOffset = 3.dp,
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 레트로 타이틀바
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .background(BackgroundGray)
                            .border(1.dp, BorderBlack)
                    )
                    Box(
                        modifier = Modifier
                            .width(29.dp)
                            .height(28.dp)
                            .background(BackgroundGray)
                            .border(1.dp, BorderBlack)
                            .noEffectClick { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("X", style = DungGeunMoBody, color = TextPrimary)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDefault)
                        .padding(16.dp)
                ) {
                    CalendarPicker(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            if (allowDeselect && date == selectedDate) {
                                selectedDate = null
                            } else {
                                selectedDate = date
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PixelShadowButton(
                            onClick = { onDismiss() },
                            backgroundColor = BackgroundGray
                        ) {
                            Text(
                                "NO", style = DungGeunMoBody, color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(50.dp))
                        PixelShadowButton(
                            onClick = { onDateConfirmed(selectedDate) },
                            backgroundColor = BackgroundGray
                        ) {
                            Text(
                                "YES", style = DungGeunMoBody, color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarBottomSheetContentPreview() {
    IkdamanTheme {
        PixelShadowBox(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            backgroundColor = BackgroundWhite,
            shadowOffset = 3.dp,
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .background(BackgroundGray)
                            .border(1.dp, BorderBlack)
                    )
                    Box(
                        modifier = Modifier
                            .width(29.dp)
                            .height(28.dp)
                            .background(BackgroundGray)
                            .border(1.dp, BorderBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("X", style = DungGeunMoBody, color = TextPrimary)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDefault)
                        .padding(16.dp)
                ) {
                    CalendarPicker(selectedDate = LocalDate.now())
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PixelShadowButton(
                            onClick = {},
                            backgroundColor = BackgroundGray
                        ) {
                            Text(
                                "NO", style = DungGeunMoBody, color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(50.dp))
                        PixelShadowButton(
                            onClick = {},
                            backgroundColor = BackgroundGray
                        ) {
                            Text(
                                "YES", style = DungGeunMoBody, color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
