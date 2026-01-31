@file:OptIn(ExperimentalMaterial3Api::class)

package project.side.ui.component

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import project.side.ui.theme.IkdamanTheme
import project.side.ui.util.noEffectClick
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun BookRegisterBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (reason: String?, startDate: java.time.LocalDate?) -> Unit
) {
    if (!show) return

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // tab: 0 = 내 서점, 1 = 히스토리
    val selectedTab = remember { mutableStateOf(0) }
    val reason = remember { mutableStateOf("") }

    // default date = today
    val today = LocalDate.now()
    val selectedDate = remember { mutableStateOf(today) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState()
    ) {
        RegisterBottomSheetUI(
            selectedTab,
            reason,
            selectedDate,
            context,
            onDismiss,
            scope,
            onConfirm
        )
    }
}

@Composable
private fun RegisterBottomSheetUI(
    selectedTab: MutableState<Int>,
    reason: MutableState<String>,
    selectedDate: MutableState<LocalDate>,
    context: Context,
    onDismiss: () -> Unit,
    scope: CoroutineScope,
    onConfirm: (String?, LocalDate?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "책 등록",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val tabShape = RoundedCornerShape(6.dp)
                val selectedBg = Color(0xFFEEEEEE)
                val unselectedBg = Color.White

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .border(width = 1.dp, color = Color.Black, shape = tabShape)
                        .background(
                            if (selectedTab.value == 0) selectedBg else unselectedBg,
                            shape = tabShape
                        )
                        .noEffectClick { selectedTab.value = 0 }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "내 서점",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.Black)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "읽고 싶은 책",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Black)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .border(width = 1.dp, color = Color.Black, shape = tabShape)
                        .background(
                            if (selectedTab.value == 1) selectedBg else unselectedBg,
                            shape = tabShape
                        )
                        .noEffectClick { selectedTab.value = 1 }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "히스토리",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.Black)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "독서 시작/완료한 책",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Black)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (selectedTab.value == 0) {
                Text(
                    "읽고 싶은 이유",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black)
                )
                Spacer(Modifier.height(8.dp))
                BasicTextField(
                    value = reason.value,
                    onValueChange = { if (it.length <= 500) reason.value = it },
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text("${reason.value.length}/500", style = TextStyle(color = Color.Gray))
            } else {
                Text("독서 시작")
                Spacer(Modifier.height(8.dp))
                val cal = Calendar.getInstance()
                cal.set(
                    selectedDate.value.year,
                    selectedDate.value.monthValue - 1,
                    selectedDate.value.dayOfMonth
                )
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .noEffectClick {
                            val dpd = DatePickerDialog(
                                context,
                                { _: DatePicker, y: Int, m: Int, d: Int ->
                                    selectedDate.value = LocalDate.of(y, m + 1, d)
                                },
                                selectedDate.value.year,
                                selectedDate.value.monthValue - 1,
                                selectedDate.value.dayOfMonth
                            )
                            dpd.show()
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.DateRange,
                        contentDescription = "calendar",
                        tint = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = selectedDate.value.format(dateFormatter), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        val reasonToSend = reason.value.takeIf { it.isNotBlank() }
                        onConfirm(reasonToSend, selectedDate.value)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black,
                )
            ) { Text("확인", style = MaterialTheme.typography.labelLarge) }
        }
    }
}

@Preview
@Composable
fun BookRegisterBottomSheetPreview() {
    IkdamanTheme {
        RegisterBottomSheetUI(
            selectedTab = remember { mutableStateOf(1) },
            reason = remember { mutableStateOf("") },
            selectedDate = remember { mutableStateOf(LocalDate.now()) },
            context = LocalContext.current,
            onDismiss = {},
            scope = rememberCoroutineScope(),
            onConfirm = { _, _ -> }
        )
    }
}
