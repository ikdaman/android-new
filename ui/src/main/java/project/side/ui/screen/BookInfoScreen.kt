package project.side.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.presentation.viewmodel.BookInfoUiState
import project.side.presentation.viewmodel.BookInfoViewModel
import androidx.compose.ui.window.Dialog
import project.side.ui.component.BookEditBottomSheet
import project.side.ui.component.RetroLoadingScreen
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.component.TitleBar
import project.side.ui.util.noEffectClick
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansBookTitleLarge
import project.side.ui.util.rememberOneClickHandler

@Composable
fun BookInfoScreen(
    viewModel: BookInfoViewModel,
    onBack: () -> Unit = {},
    onDeleteComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) onDeleteComplete()
    }

    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            PixelShadowBox(
                modifier = Modifier.fillMaxWidth(),
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
                                .border(1.dp, BorderBlack)
                                .noEffectClick { showDeleteDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("X", style = DungGeunMoBody, color = TextPrimary)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundDefault)
                            .padding(20.dp)
                    ) {
                        Text("책 삭제", style = DungGeunMoPopupTitle, color = TextPrimary)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "책을 삭제하면 모든 기록이 사라져요.\n정말로 삭제하시겠어요?",
                            style = WantedSansBody,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PixelShadowButton(
                                onClick = { showDeleteDialog = false },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "NO", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(50.dp))
                            PixelShadowButton(
                                onClick = {
                                    showDeleteDialog = false
                                    viewModel.deleteBook()
                                },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "YES", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    RetroLoadingScreen(isLoading = uiState is BookInfoUiState.Loading) {
        when (val state = uiState) {
            is BookInfoUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "오류가 발생했습니다.", color = Primary)
                }
            }
            is BookInfoUiState.Success -> {
                BookEditBottomSheet(
                    show = showEditSheet,
                    detail = state.detail,
                    onDismiss = { showEditSheet = false },
                    onConfirm = { result ->
                        showEditSheet = false
                        viewModel.updateMyBook(
                            status = result.status,
                            reason = result.reason,
                            startedDate = result.startedDate,
                            finishedDate = result.finishedDate,
                            bookInfoTitle = result.bookInfoTitle,
                            bookInfoAuthor = result.bookInfoAuthor,
                            bookInfoPublisher = result.bookInfoPublisher,
                            bookInfoPublishDate = result.bookInfoPublishDate,
                            bookInfoIsbn = result.bookInfoIsbn,
                            bookInfoTotalPage = result.bookInfoTotalPage
                        )
                    }
                )
                BookInfoContent(
                    detail = state.detail,
                    onBack = onBack,
                    onEdit = { showEditSheet = true },
                    onDelete = { showDeleteDialog = true }
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun BookInfoContent(
    detail: MyBookDetail,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val isHistory = detail.shelfType == "HISTORY"
    val oneClickHandler = rememberOneClickHandler()
    val tagText = when {
        isHistory && detail.readingStatus == "COMPLETED" -> "완독"
        isHistory -> "읽는 중"
        else -> "읽다만"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
    ) {
        TitleBar(
            title = "",
            showBackButton = true,
            onBackButtonClicked = { oneClickHandler { onBack() } },
            rightText = "삭제",
            onRightClick = onDelete
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Book cover
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = detail.bookInfo.coverImage,
                    contentDescription = "book cover",
                    modifier = Modifier
                        .width(210.dp)
                        .height(272.dp),
                    placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                    fallback = ColorPainter(Color.Gray.copy(alpha = 0.5f))
                )
            }

            Spacer(Modifier.height(20.dp))

            // Tag badge
            Box(
                modifier = Modifier
                    .background(Primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tagText,
                    style = DungGeunMoTag,
                    color = TextWhite
                )
            }

            Spacer(Modifier.height(8.dp))

            // Book title & author
            Text(
                text = detail.bookInfo.title,
                style = WantedSansBookTitleLarge,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(text = detail.bookInfo.author, style = WantedSansBody, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            if (detail.bookInfo.publisher != null) {
                Text(text = detail.bookInfo.publisher!!, style = WantedSansBody, color = TextPrimary)
            }

            Spacer(Modifier.height(30.dp))

            // 독서 이력 section
            SectionWithHeader(
                title = "독서 이력",
                onEditClick = onEdit
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    InfoRow("SAVE", detail.createdDate.take(10).replace("-", " - "))
                    Spacer(Modifier.height(8.dp))
                    InfoRow("START", detail.historyInfo.startedDate?.take(10)?.replace("-", " - ") ?: "")
                    Spacer(Modifier.height(8.dp))
                    val finishText = when {
                        detail.historyInfo.finishedDate != null -> detail.historyInfo.finishedDate!!.take(10).replace("-", " - ")
                        detail.historyInfo.startedDate != null -> "읽는 중"
                        else -> ""
                    }
                    InfoRow("FINISH", finishText)
                }
            }

            Spacer(Modifier.height(20.dp))

            // 읽고 싶었던 이유 section
            SectionWithHeader(
                title = "읽고 싶었던 이유",
                onEditClick = onEdit
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text(
                        text = detail.reason ?: "-",
                        style = WantedSansBody,
                        color = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Book detail fields with shadow
            ShadowInfoField("페이지 수", if (detail.bookInfo.totalPage != null) "${detail.bookInfo.totalPage}" else "-")
            Spacer(Modifier.height(20.dp))
            ShadowInfoField("출간일", detail.bookInfo.publishDate?.take(10)?.replace("-", " - ") ?: "-")
            Spacer(Modifier.height(20.dp))
            ShadowInfoField("ISBN", detail.bookInfo.isbn ?: "-")
            Spacer(Modifier.height(20.dp))
            ShadowInfoField("책 소개", detail.bookInfo.description ?: "-")

            Spacer(Modifier.height(20.dp))

            // Aladin link
            if (detail.bookInfo.aladinId != null) {
                val context = LocalContext.current
                val aladinUrl = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=${detail.bookInfo.aladinId}&partner=openAPI&start=api"
                ShadowBox {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(aladinUrl)))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("알라딘에서 더보기", style = WantedSansBody, color = TextPrimary, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Section with gray header bar (독서 이력, 읽고 싶었던 이유)
 */
@Composable
private fun SectionWithHeader(
    title: String,
    onEditClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(BorderBlack)
        )
        // Foreground
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .border(1.dp, BorderBlack)
        ) {
            // Gray header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(BackgroundGray)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp)
                        .padding(start = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = title, style = DungGeunMoSubtitle, color = TextPrimary)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(22.dp)
                        .background(BorderBlack)
                )
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(22.dp)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "수정", style = DungGeunMoSubtitle, color = TextPrimary)
                }
            }
            // Separator
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderBlack))
            // Content
            content()
        }
    }
}

/**
 * Info row with DungGeunMo label + Wanted Sans value
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = DungGeunMoBody.copy(letterSpacing = 3.2.sp),
            color = TextPrimary,
            modifier = Modifier.width(74.dp)
        )
        Text(text = value, style = WantedSansBody, color = TextPrimary)
    }
}

/**
 * Info field with label above and shadow box
 */
@Composable
private fun ShadowInfoField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = DungGeunMoSubtitle, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        ShadowBox {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(text = value, style = WantedSansBody, color = TextPrimary)
            }
        }
    }
}

/**
 * White box with black angular offset shadow (retro pixel style)
 */
@Composable
private fun ShadowBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Shadow (offset black box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(BorderBlack)
        )
        // White foreground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .border(1.dp, BorderBlack)
        ) {
            content()
        }
    }
}

private val dummyStoreDetail = MyBookDetail(
    mybookId = "1", readingStatus = "WISH", shelfType = "STORE", createdDate = "2026-02-14",
    reason = "나는 왜냐하면 이 책을 읽고 싶었기 때문이다.",
    bookInfo = MyBookDetailBookInfo(bookId = "100", source = "ALADIN", title = "소년과 두더지와 여우와 말", author = "찰리 맥커시",
        coverImage = null, publisher = "상상의 힘", totalPage = 234, publishDate = "2020-04-20",
        isbn = "9788997381678", aladinId = "123456789", description = "이 책은 소년과 두더지와 여우와 말의 이야기입니다."),
    historyInfo = MyBookDetailHistoryInfo(startedDate = "2025-01-25", finishedDate = null)
)

@Preview(showBackground = true)
@Composable
fun BookInfoScreenStorePreview() {
    IkdamanTheme { BookInfoContent(detail = dummyStoreDetail) }
}
