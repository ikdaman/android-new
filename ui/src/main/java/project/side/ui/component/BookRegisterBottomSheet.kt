package project.side.ui.component

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Calendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        sheetState = remember { ModalBottomSheetDefaults.rememberModalBottomSheetState() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("책 등록", style = TextStyle(color = Color.Black))
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { selectedTab.value = 0 }, modifier = Modifier.weight(1f)) {
                        Text("내 서점")
                    }
                    Button(onClick = { selectedTab.value = 1 }, modifier = Modifier.weight(1f)) {
                        Text("히스토리")
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (selectedTab.value == 0) {
                    Text("읽고 싶은 이유")
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
                    cal.set(selectedDate.value.year, selectedDate.value.monthValue - 1, selectedDate.value.dayOfMonth)
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedDate.value.format(dateFormatter))
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
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
                        }) {
                            Text("날짜 선택")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onDismiss() }) { Text("취소") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        scope.launch {
                            val reasonToSend = reason.value.takeIf { it.isNotBlank() }
                            onConfirm(reasonToSend, selectedDate.value)
                        }
                    }) { Text("확인") }
                }
            }
        }
    }
}
