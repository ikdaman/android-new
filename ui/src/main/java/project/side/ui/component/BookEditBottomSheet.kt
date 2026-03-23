@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.util.noEffectClick
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class BookEditResult(
    val status: String?,
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
        BookEditBottomSheetContent(detail = detail, onDismiss = onDismiss, onConfirm = onConfirm)
    }
}

@Composable
internal fun BookEditBottomSheetContent(
    detail: MyBookDetail,
    onDismiss: () -> Unit = {},
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
    val publishDate = remember {
        mutableStateOf(
            detail.bookInfo.publishDate?.let {
                try { LocalDate.parse(it.take(10)) } catch (_: Exception) { null }
            }
        )
    }
    val isbn = remember { mutableStateOf(detail.bookInfo.isbn ?: "") }
    val totalPage = remember { mutableStateOf(detail.bookInfo.totalPage?.toString() ?: "") }

    var showStartCalendar by remember { mutableStateOf(false) }
    var showEndCalendar by remember { mutableStateOf(false) }
    var showPublishDateCalendar by remember { mutableStateOf(false) }

    CalendarBottomSheet(
        show = showStartCalendar,
        initialDate = startDate.value,
        onDismiss = { showStartCalendar = false },
        onDateConfirmed = { date ->
            if (date != null) startDate.value = date
            showStartCalendar = false
        }
    )

    CalendarBottomSheet(
        show = showEndCalendar,
        initialDate = endDate.value ?: LocalDate.now(),
        allowDeselect = true,
        onDismiss = { showEndCalendar = false },
        onDateConfirmed = { date ->
            endDate.value = date
            showEndCalendar = false
        }
    )

    CalendarBottomSheet(
        show = showPublishDateCalendar,
        initialDate = publishDate.value ?: LocalDate.now(),
        onDismiss = { showPublishDateCalendar = false },
        onDateConfirmed = { date ->
            publishDate.value = date
            showPublishDateCalendar = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        PixelShadowBox(
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

                // 본문 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDefault)
                        .padding(16.dp)
                ) {
                    Text(
                        "책 정보 수정",
                        style = DungGeunMoPopupTitle,
                    )
                    Spacer(Modifier.height(16.dp))

                    // 탭 선택 (pill 스타일)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val selectedBg = Color(0xFFE4E4E4)
                        val unselectedBg = BackgroundGray
                        PixelShadowButton(
                            onClick = { selectedTab.value = 0 },
                            backgroundColor = if (selectedTab.value == 0) selectedBg else unselectedBg,
                            isSelected = selectedTab.value == 0,
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
                            isSelected = selectedTab.value == 1,
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
                        // 내 서점 탭: 읽고 싶은 이유
                        Text("*읽고 싶은 책이에요.", style = DungGeunMoSubtitle)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(188.dp)
                                .background(BackgroundWhite)
                                .border(1.dp, BorderBlack)
                                .padding(8.dp)
                        ) {
                            if (reason.value.isEmpty()) {
                                Text(
                                    "읽고 싶은 이유를 작성해주세요.",
                                    style = WantedSansBody,
                                    color = TextHint
                                )
                            }
                            BasicTextField(
                                value = reason.value,
                                onValueChange = { if (it.length <= 400) reason.value = it },
                                textStyle = WantedSansBody.copy(color = TextPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${reason.value.length}/400", style = DungGeunMoTag.copy(color = TextHint))
                    } else {
                        // 히스토리 탭: 독서 시작/종료 + 이유
                        Text("독서 시작", style = DungGeunMoSubtitle)
                        Spacer(Modifier.height(8.dp))
                        PixelShadowButton(
                            onClick = { showStartCalendar = true },
                            backgroundColor = BackgroundWhite,
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                startDate.value.format(dateFormatter),
                                style = DungGeunMoBody,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                        Text("독서 종료", style = DungGeunMoSubtitle)
                        Spacer(Modifier.height(8.dp))
                        PixelShadowButton(
                            onClick = { showEndCalendar = true },
                            backgroundColor = BackgroundWhite,
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                endDate.value?.format(dateFormatter) ?: "읽는 중",
                                style = DungGeunMoBody,
                                color = if (endDate.value != null) TextPrimary else TextHint,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("읽고 싶은 이유", style = DungGeunMoSubtitle)
                        Spacer(Modifier.height(8.dp))
                        BasicTextField(
                            value = reason.value,
                            onValueChange = { if (it.length <= 400) reason.value = it },
                            textStyle = WantedSansBody.copy(color = TextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("${reason.value.length}/400", style = DungGeunMoTag.copy(color = TextHint))
                    }

                    // CUSTOM 책: bookInfo 편집 필드
                    if (isCustom) {
                        Spacer(Modifier.height(24.dp))
                        Text("책 정보", style = DungGeunMoSubtitle)
                        Spacer(Modifier.height(8.dp))

                        EditInputField(label = "제목", value = title.value, onValueChange = { title.value = it })
                        Spacer(Modifier.height(12.dp))
                        EditInputField(label = "작가", value = author.value, onValueChange = { author.value = it })
                        Spacer(Modifier.height(12.dp))
                        EditInputField(label = "출판사", value = publisher.value, onValueChange = { publisher.value = it })
                        Spacer(Modifier.height(12.dp))
                        Column {
                            Text("출간일", style = DungGeunMoSubtitle)
                            Spacer(Modifier.height(4.dp))
                            PixelShadowButton(
                                onClick = { showPublishDateCalendar = true },
                                backgroundColor = BackgroundWhite,
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    publishDate.value?.format(dateFormatter) ?: "YYYY-MM-DD",
                                    style = WantedSansBodySmall,
                                    color = if (publishDate.value != null) Color.Black else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        EditInputField(label = "ISBN", value = isbn.value, onValueChange = { isbn.value = it }, keyboardType = KeyboardType.Number)
                        Spacer(Modifier.height(12.dp))
                        EditInputField(label = "페이지 수", value = totalPage.value, onValueChange = { totalPage.value = it }, keyboardType = KeyboardType.Number)
                    }

                    Spacer(Modifier.height(32.dp))

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
                                val reasonToSend = reason.value.takeIf { it.isNotBlank() }
                                val statusToSend = if (selectedTab.value == 0) "STORE" else "HISTORY"
                                val result = if (selectedTab.value == 0) {
                                    BookEditResult(
                                        status = statusToSend,
                                        reason = reasonToSend,
                                        startedDate = null, finishedDate = null,
                                        bookInfoTitle = title.value,
                                        bookInfoAuthor = author.value,
                                        bookInfoPublisher = publisher.value.ifBlank { null },
                                        bookInfoPublishDate = publishDate.value?.format(dateFormatter),
                                        bookInfoIsbn = isbn.value.ifBlank { null },
                                        bookInfoTotalPage = totalPage.value.toIntOrNull()
                                    )
                                } else {
                                    BookEditResult(
                                        status = statusToSend,
                                        reason = reasonToSend,
                                        startedDate = startDate.value.atStartOfDay(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
                                        finishedDate = endDate.value?.atStartOfDay(java.time.ZoneOffset.UTC)?.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
                                        bookInfoTitle = title.value,
                                        bookInfoAuthor = author.value,
                                        bookInfoPublisher = publisher.value.ifBlank { null },
                                        bookInfoPublishDate = publishDate.value?.format(dateFormatter),
                                        bookInfoIsbn = isbn.value.ifBlank { null },
                                        bookInfoTotalPage = totalPage.value.toIntOrNull()
                                    )
                                }
                                onConfirm(result)
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
        Text(label, style = DungGeunMoSubtitle)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = WantedSansBodySmall.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.05f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, style = WantedSansBodySmall, color = Color.Gray)
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
                    totalPage = 300, publishDate = "2025-01-01", isbn = "1234567890", aladinId = null, description = null
                ),
                historyInfo = MyBookDetailHistoryInfo(startedDate = null, finishedDate = null)
            )
        )
    }
}
