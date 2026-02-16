@file:OptIn(ExperimentalMaterial3Api::class)

package project.side.ui.component

import android.app.DatePickerDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.ui.theme.IkdamanTheme
import project.side.ui.util.noEffectClick
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class BookEditResult(
    val reason: String?,
    val startedDate: String?,
    val finishedDate: String?,
    val bookInfoTitle: String?,
    val bookInfoAuthor: String?,
    val bookInfoPublisher: String?,
    val bookInfoPublishDate: String?,
    val bookInfoIsbn: String?,
    val bookInfoTotalPage: Int?
)

@Composable
fun BookEditBottomSheet(
    show: Boolean,
    detail: MyBookDetail,
    onDismiss: () -> Unit,
    onConfirm: (BookEditResult) -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp)
    ) {
        BookEditBottomSheetContent(detail = detail, onConfirm = onConfirm)
    }
}

@Composable
internal fun BookEditBottomSheetContent(
    detail: MyBookDetail,
    onConfirm: (BookEditResult) -> Unit = {}
) {
    val isHistory = detail.shelfType == "HISTORY"
    val isCustom = detail.bookInfo.source == "CUSTOM"

    // tab: 0 = 내 서점, 1 = 히스토리
    val selectedTab = remember { mutableStateOf(if (isHistory) 1 else 0) }
    val reason = remember { mutableStateOf(detail.reason ?: "") }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = remember {
        mutableStateOf(
            detail.historyInfo.startedDate?.let {
                try { LocalDate.parse(it.take(10)) } catch (_: Exception) { LocalDate.now() }
            } ?: LocalDate.now()
        )
    }
    val endDate = remember {
        mutableStateOf(
            detail.historyInfo.finishedDate?.let {
                try { LocalDate.parse(it.take(10)) } catch (_: Exception) { null }
            }
        )
    }

    // CUSTOM book info fields
    val title = remember { mutableStateOf(detail.bookInfo.title) }
    val author = remember { mutableStateOf(detail.bookInfo.author) }
    val publisher = remember { mutableStateOf(detail.bookInfo.publisher ?: "") }
    val publishDate = remember { mutableStateOf(detail.bookInfo.publishDate ?: "") }
    val isbn = remember { mutableStateOf(detail.bookInfo.isbn ?: "") }
    val totalPage = remember { mutableStateOf(detail.bookInfo.totalPage?.toString() ?: "") }

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "책 정보 수정",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            // 탭 선택
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
                    Text("내 서점", style = MaterialTheme.typography.labelLarge.copy(color = Color.Black))
                    Spacer(Modifier.height(4.dp))
                    Text("읽고 싶은 책", style = MaterialTheme.typography.labelSmall.copy(color = Color.Black))
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
                    Text("히스토리", style = MaterialTheme.typography.labelLarge.copy(color = Color.Black))
                    Spacer(Modifier.height(4.dp))
                    Text("독서 시작/완료한 책", style = MaterialTheme.typography.labelSmall.copy(color = Color.Black))
                }
            }

            Spacer(Modifier.height(32.dp))

            if (selectedTab.value == 0) {
                // 내 서점 탭: 읽고 싶은 이유
                Text("읽고 싶은 이유", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
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
                // 히스토리 탭: 독서 시작/종료 + 이유
                Text("독서 시작", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
                Spacer(Modifier.height(8.dp))
                DateRow(date = startDate, dateFormatter = dateFormatter) {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, y: Int, m: Int, d: Int ->
                            startDate.value = LocalDate.of(y, m + 1, d)
                        },
                        startDate.value.year,
                        startDate.value.monthValue - 1,
                        startDate.value.dayOfMonth
                    ).show()
                }

                Spacer(Modifier.height(32.dp))
                Text("독서 종료", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .noEffectClick {
                            val initial = endDate.value ?: LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _: DatePicker, y: Int, m: Int, d: Int ->
                                    endDate.value = LocalDate.of(y, m + 1, d)
                                },
                                initial.year,
                                initial.monthValue - 1,
                                initial.dayOfMonth
                            ).show()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = "calendar-end", tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    if (endDate.value == null) {
                        Text("읽는 중", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                    } else {
                        Text(endDate.value!!.format(dateFormatter), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(32.dp))
                Text("읽고 싶은 이유", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
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
            }

            // CUSTOM 책: bookInfo 편집 필드
            if (isCustom) {
                Spacer(Modifier.height(24.dp))
                Text("책 정보", style = MaterialTheme.typography.titleSmall.copy(color = Color.Black))
                Spacer(Modifier.height(8.dp))

                EditInputField(label = "제목", value = title.value, onValueChange = { title.value = it })
                Spacer(Modifier.height(12.dp))
                EditInputField(label = "작가", value = author.value, onValueChange = { author.value = it })
                Spacer(Modifier.height(12.dp))
                EditInputField(label = "출판사", value = publisher.value, onValueChange = { publisher.value = it })
                Spacer(Modifier.height(12.dp))
                EditInputField(label = "출간일", value = publishDate.value, onValueChange = { publishDate.value = it }, placeholder = "YYYY-MM-DD")
                Spacer(Modifier.height(12.dp))
                EditInputField(label = "ISBN", value = isbn.value, onValueChange = { isbn.value = it }, keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                EditInputField(label = "페이지 수", value = totalPage.value, onValueChange = { totalPage.value = it }, keyboardType = KeyboardType.Number)
            }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    val reasonToSend = reason.value.takeIf { it.isNotBlank() }
                    val result = if (selectedTab.value == 0) {
                        BookEditResult(
                            reason = reasonToSend,
                            startedDate = null, finishedDate = null,
                            bookInfoTitle = if (isCustom) title.value else null,
                            bookInfoAuthor = if (isCustom) author.value else null,
                            bookInfoPublisher = if (isCustom) publisher.value.ifBlank { null } else null,
                            bookInfoPublishDate = if (isCustom) publishDate.value.ifBlank { null } else null,
                            bookInfoIsbn = if (isCustom) isbn.value.ifBlank { null } else null,
                            bookInfoTotalPage = if (isCustom) totalPage.value.toIntOrNull() else null
                        )
                    } else {
                        BookEditResult(
                            reason = reasonToSend,
                            startedDate = startDate.value.format(dateFormatter),
                            finishedDate = endDate.value?.format(dateFormatter),
                            bookInfoTitle = if (isCustom) title.value else null,
                            bookInfoAuthor = if (isCustom) author.value else null,
                            bookInfoPublisher = if (isCustom) publisher.value.ifBlank { null } else null,
                            bookInfoPublishDate = if (isCustom) publishDate.value.ifBlank { null } else null,
                            bookInfoIsbn = if (isCustom) isbn.value.ifBlank { null } else null,
                            bookInfoTotalPage = if (isCustom) totalPage.value.toIntOrNull() else null
                        )
                    }
                    onConfirm(result)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)
            ) {
                Text("저장", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun DateRow(
    date: MutableState<LocalDate>,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .noEffectClick { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        Icon(imageVector = Icons.Filled.DateRange, contentDescription = "calendar", tint = Color.Black)
        Spacer(Modifier.width(8.dp))
        Text(text = date.value.format(dateFormatter), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun EditInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.05f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                innerTextField()
            }
        )
    }
}

@Preview
@Composable
fun BookEditBottomSheetPreview() {
    IkdamanTheme {
        BookEditBottomSheetContent(
            detail = MyBookDetail(
                mybookId = "1",
                readingStatus = "TODO",
                shelfType = "STORE",
                createdDate = "2026-01-01",
                reason = "재미있어 보여서",
                bookInfo = MyBookDetailBookInfo(
                    bookId = "1", source = "CUSTOM", title = "테스트 책",
                    author = "테스트 작가", coverImage = null, publisher = "출판사",
                    totalPage = 300, publishDate = "2025-01-01", isbn = "1234567890", aladinId = null
                ),
                historyInfo = MyBookDetailHistoryInfo(startedDate = null, finishedDate = null)
            )
        )
    }
}
