@file:OptIn(ExperimentalMaterial3Api::class)

package project.side.ui.component

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.util.noEffectClick
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookRegisterBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (reason: String?, startDate: LocalDate?, endDate: LocalDate?) -> Unit
) {
    if (!show) return

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedTab = remember { mutableStateOf(0) }
    val reason = remember { mutableStateOf("") }
    val today = LocalDate.now()
    val selectedDate = remember { mutableStateOf(today) }
    val selectedEndDate = remember { mutableStateOf<LocalDate?>(null) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(),
        dragHandle = null,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp)
    ) {
        RegisterBottomSheetUI(
            selectedTab,
            reason,
            selectedDate,
            selectedEndDate,
            context,
            scope,
            onDismiss,
            onConfirm
        )
    }
}

@Composable
private fun RegisterBottomSheetUI(
    selectedTab: MutableState<Int>,
    reason: MutableState<String>,
    selectedDate: MutableState<LocalDate>,
    selectedEndDate: MutableState<LocalDate?>,
    context: Context,
    scope: CoroutineScope,
    onDismiss: () -> Unit = {},
    onConfirm: (String?, LocalDate?, LocalDate?) -> Unit
) {
    // 팝업 전체에 픽셀 그림자 적용
    PixelShadowBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = BackgroundWhite,
        shadowOffset = 3.dp,
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 상단바: 타이틀 바 + X 닫기 버튼
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

            // body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDefault)
                    .padding(20.dp)
            ) {
                // 제목
                Text(
                    "책 추가",
                    style = DungGeunMoPopupTitle,
                    color = TextPrimary
                )

                Spacer(Modifier.height(20.dp))

                // 탭: 내 서점 / 히스토리
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val selectedBg = Color(0xFFE4E4E4)
                    val unselectedBg = BackgroundGray

                    PixelShadowButton(
                        onClick = { selectedTab.value = 0 },
                        backgroundColor = if (selectedTab.value == 0) selectedBg else unselectedBg,
                    ) {
                        Text(
                            "내 서점",
                            style = DungGeunMoSubtitle,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    PixelShadowButton(
                        onClick = { selectedTab.value = 1 },
                        backgroundColor = if (selectedTab.value == 1) selectedBg else unselectedBg,
                    ) {
                        Text(
                            "히스토리",
                            style = DungGeunMoSubtitle,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (selectedTab.value == 0) {
                    // 내 서점 탭
                    Text(
                        "*읽고 싶은 책이에요.",
                        style = DungGeunMoSubtitle,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(12.dp))

                    // 메모 입력란
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(188.dp)
                            .background(BackgroundWhite)
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = reason.value,
                            onValueChange = { if (it.length <= 400) reason.value = it },
                            textStyle = WantedSansBody.copy(color = TextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (reason.value.isEmpty()) {
                                        Text(
                                            "읽고 싶은 이유를 작성해주세요.",
                                            style = WantedSansBody.copy(
                                                color = TextPrimary.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        Text(
                            "${reason.value.length}/400",
                            style = WantedSansBodySmall.copy(
                                fontSize = 10.sp,
                                color = TextPrimary.copy(alpha = 0.6f)
                            ),
                            textAlign = TextAlign.End,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                } else {
                    // 히스토리 탭
                    Text(
                        "*독서 중이거나 완독한 책이에요.",
                        style = DungGeunMoSubtitle,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(12.dp))

                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")

                    DatePickerRow(
                        label = "START",
                        dateText = selectedDate.value.format(dateFormatter),
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _: DatePicker, y: Int, m: Int, d: Int ->
                                    selectedDate.value = LocalDate.of(y, m + 1, d)
                                },
                                selectedDate.value.year,
                                selectedDate.value.monthValue - 1,
                                selectedDate.value.dayOfMonth
                            ).show()
                        }
                    )

                    Spacer(Modifier.height(20.dp))

                    DatePickerRow(
                        label = "FINISH",
                        dateText = selectedEndDate.value?.format(dateFormatter) ?: "읽는 중",
                        onClick = {
                            val initial = selectedEndDate.value ?: LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _: DatePicker, y: Int, m: Int, d: Int ->
                                    selectedEndDate.value = LocalDate.of(y, m + 1, d)
                                },
                                initial.year,
                                initial.monthValue - 1,
                                initial.dayOfMonth
                            ).show()
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // NO / YES 버튼
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
                        onClick = {
                            scope.launch {
                                val reasonToSend = reason.value.takeIf { it.isNotBlank() }
                                if (selectedTab.value == 0) {
                                    onConfirm(reasonToSend, null, null)
                                } else {
                                    onConfirm(
                                        reasonToSend,
                                        selectedDate.value,
                                        selectedEndDate.value
                                    )
                                }
                            }
                        },
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

@Composable
private fun DatePickerRow(
    label: String,
    dateText: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = DungGeunMoBody,
            color = TextPrimary,
            modifier = Modifier.width(60.dp)
        )

        Spacer(Modifier.width(20.dp))

        PixelShadowButton(
            onClick = { onClick() },
            backgroundColor = BackgroundWhite,
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(180.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = dateText,
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
}

@Preview
@Composable
fun BookRegisterBottomSheetPreview() {
    IkdamanTheme {
        RegisterBottomSheetUI(
            selectedTab = remember { mutableStateOf(0) },
            reason = remember { mutableStateOf("") },
            selectedDate = remember { mutableStateOf(LocalDate.now()) },
            context = LocalContext.current,
            scope = rememberCoroutineScope(),
            selectedEndDate = remember { mutableStateOf(null) },
            onConfirm = { _, _, _ -> },
        )
    }
}
