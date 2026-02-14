package project.side.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.presentation.viewmodel.BookInfoUiState
import project.side.presentation.viewmodel.BookInfoViewModel
import project.side.ui.theme.IkdamanTheme

@Composable
fun BookInfoScreen(
    viewModel: BookInfoViewModel,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDeleteComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            onDeleteComplete()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("책 삭제") },
            text = { Text("책을 삭제하면 모든 기록이 사라져요.\n삭제하시겠어요?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteBook()
                }) {
                    Text("삭제", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    when (val state = uiState) {
        is BookInfoUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is BookInfoUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message ?: "오류가 발생했습니다.", color = Color.Red)
            }
        }
        is BookInfoUiState.Success -> {
            BookInfoContent(
                detail = state.detail,
                onBack = onBack,
                onEdit = onEdit,
                onDelete = { showDeleteDialog = true }
            )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 상단 바: 뒤로가기 + 수정/삭제
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            Row {
                TextButton(onClick = onEdit) {
                    Text("수정", color = Color.Black)
                }
                TextButton(onClick = onDelete) {
                    Text("삭제", color = Color.Red)
                }
            }
        }

        // 썸네일
        AsyncImage(
            model = detail.bookInfo.coverImage,
            contentDescription = "book cover",
            modifier = Modifier
                .width(131.dp)
                .height(181.dp)
                .align(Alignment.CenterHorizontally),
            placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
            fallback = ColorPainter(Color.Gray.copy(alpha = 0.5f))
        )

        Spacer(Modifier.height(12.dp))

        // 책 제목
        Text(
            text = detail.bookInfo.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(4.dp))

        // 책 타입
        Text(
            text = if (isHistory) "히스토리" else "내 서점",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // 타입별 정보
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (isHistory) {
                // 히스토리: 시작일, 완독일, 이유
                InfoRow("독서 시작일", detail.historyInfo.startedDate ?: "-")
                Spacer(Modifier.height(16.dp))
                InfoRow("완독일", detail.historyInfo.finishedDate ?: "읽는 중")
                Spacer(Modifier.height(16.dp))
                InfoRow("읽고 싶었던 이유", detail.reason ?: "-")
            } else {
                // 내 서점: 담은 날, 이유
                InfoRow("내 서점에 담은 날", detail.createdDate)
                Spacer(Modifier.height(16.dp))
                InfoRow("읽고 싶었던 이유", detail.reason ?: "-")
            }

            Spacer(Modifier.height(24.dp))

            // 공통 책 정보
            InfoRow("책 제목", detail.bookInfo.title)
            Spacer(Modifier.height(16.dp))
            InfoRow("작가", detail.bookInfo.author)
            Spacer(Modifier.height(16.dp))
            InfoRow("출판사", detail.bookInfo.publisher ?: "-")
            Spacer(Modifier.height(16.dp))
            if (detail.bookInfo.aladinId != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("알라딘에서 보기", style = MaterialTheme.typography.labelMedium)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            InfoRow("페이지 수", if (detail.bookInfo.totalPage != null) "${detail.bookInfo.totalPage}p" else "-")
            Spacer(Modifier.height(16.dp))
            InfoRow("출간일", detail.bookInfo.publishDate ?: "-")
            Spacer(Modifier.height(16.dp))
            InfoRow("ISBN", detail.bookInfo.isbn ?: "-")
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

// Preview용 더미 데이터
private val dummyStoreDetail = MyBookDetail(
    mybookId = "1",
    readingStatus = "WISH",
    shelfType = "STORE",
    createdDate = "2026-02-14",
    reason = "디스토피아 소설의 고전이라고 해서 읽어보고 싶었습니다.",
    bookInfo = MyBookDetailBookInfo(
        bookId = "100",
        source = "ALADIN",
        title = "1984",
        author = "조지 오웰",
        coverImage = "https://image.aladin.co.kr/product/123/45/cover/9788936433598_1.jpg",
        publisher = "민음사",
        totalPage = 408,
        publishDate = "2003-04-15",
        isbn = "9788936433598",
        aladinId = "123456789"
    ),
    historyInfo = MyBookDetailHistoryInfo(
        startedDate = null,
        finishedDate = null
    )
)

private val dummyHistoryDetail = MyBookDetail(
    mybookId = "2",
    readingStatus = "READING",
    shelfType = "HISTORY",
    createdDate = "2026-01-10",
    reason = "친구가 강력하게 추천해서 읽기 시작했습니다.",
    bookInfo = MyBookDetailBookInfo(
        bookId = "200",
        source = "ALADIN",
        title = "데미안",
        author = "헤르만 헤세",
        coverImage = null,
        publisher = "민음사",
        totalPage = 232,
        publishDate = "2000-05-15",
        isbn = "9788937460494",
        aladinId = "987654321"
    ),
    historyInfo = MyBookDetailHistoryInfo(
        startedDate = "2026-02-01",
        finishedDate = null
    )
)

@Preview(showBackground = true)
@Composable
fun BookInfoScreenStorePreview() {
    IkdamanTheme {
        BookInfoContent(detail = dummyStoreDetail)
    }
}

@Preview(showBackground = true)
@Composable
fun BookInfoScreenHistoryPreview() {
    IkdamanTheme {
        BookInfoContent(detail = dummyHistoryDetail)
    }
}
