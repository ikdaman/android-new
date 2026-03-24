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
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.util.noEffectClick
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReadingStartBottomSheet(
    show: Boolean,
    bookTitle: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReadingStartBottomSheetContent(
            bookTitle = bookTitle,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Composable
internal fun ReadingStartBottomSheetContent(
    bookTitle: String = "",
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
            if (date != null) startDate = date
            showCalendar = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        PixelShadowBox(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = BackgroundWhite,
            shadowOffset = 3.dp,
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Retro top bar
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

                // Body content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDefault)
                        .padding(20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Text(
                        "책 시작하기",
                        style = DungGeunMoPopupTitle,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(35.dp))
                    Text(
                        bookTitle,
                        style = WantedSansBody,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("START", style = DungGeunMoBody, color = TextPrimary, modifier = Modifier.width(64.dp))
                        PixelShadowButton(
                            onClick = { showCalendar = true },
                            backgroundColor = BackgroundWhite,
                            modifier = Modifier.weight(1f),
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        startDate.format(dateFormatter),
                                        style = DungGeunMoBody,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .width(28.dp)
                                        .background(BackgroundGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("▼", style = DungGeunMoSubtitle, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("FINISH", style = DungGeunMoBody, color = TextPrimary, modifier = Modifier.width(64.dp))
                        PixelShadowBox(
                            backgroundColor = BackgroundWhite,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        "읽는 중",
                                        style = DungGeunMoBody,
                                        color = TextHint,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .width(28.dp)
                                        .background(BackgroundGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("▼", style = DungGeunMoSubtitle, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(43.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PixelShadowButton(
                            onClick = { onDismiss() },
                            backgroundColor = BackgroundGray,
                        ) {
                            Text(
                                "NO",
                                style = DungGeunMoBody,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(50.dp))
                        PixelShadowButton(
                            onClick = { onConfirm() },
                            backgroundColor = BackgroundGray,
                        ) {
                            Text(
                                "YES",
                                style = DungGeunMoBody,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ReadingStartBottomSheetPreview() {
    IkdamanTheme {
        ReadingStartBottomSheetContent(
            bookTitle = "책 제목이 들어갑니다",
            onDismiss = {},
            onConfirm = {}
        )
    }
}
