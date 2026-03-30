package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import project.side.ui.component.RetroLoadingScreen
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import project.side.domain.model.HistoryBookInfo
import project.side.presentation.model.HistoryBookState
import project.side.presentation.model.HistoryViewType
import project.side.presentation.viewmodel.HistoryViewModel
import project.side.ui.R
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBookClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    HistoryScreenUI(
        uiState = uiState,
        onViewTypeChanged = viewModel::onViewTypeChanged,
        onToggleSort = viewModel::toggleSort,
        onLoadMore = { viewModel.loadMore() },
        onRetry = { viewModel.getBooks() },
        onBookClick = onBookClick,
        onSearchClick = onSearchClick
    )
}

@Composable
fun HistoryScreenUI(
    uiState: HistoryBookState = HistoryBookState(),
    onViewTypeChanged: () -> Unit = {},
    onToggleSort: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onRetry: () -> Unit = {},
    onBookClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    RetroLoadingScreen(isLoading = uiState.isLoading) {
    if (uiState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = uiState.errorMessage ?: "", style = DungGeunMoBody, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onRetry) {
                    Text("다시 시도", style = DungGeunMoSubtitle)
                }
            }
        }
    } else {
        val isListView = uiState.viewType == HistoryViewType.LIST

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDefault)
        ) {
            TitleBar("히스토리")

            // Toolbar: view toggle (left) + sort (right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View toggle buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val listInteraction = remember { MutableInteractionSource() }
                    val listPressed by listInteraction.collectIsPressedAsState()
                    val gridInteraction = remember { MutableInteractionSource() }
                    val gridPressed by gridInteraction.collectIsPressedAsState()

                    val showListPressed = isListView || listPressed
                    val showGridPressed = !isListView || gridPressed

                    Image(
                        painter = painterResource(
                            if (showListPressed) R.drawable.ic_list_button_p
                            else R.drawable.ic_list_button
                        ),
                        contentDescription = "리스트 뷰",
                        modifier = Modifier
                            .size(if (showListPressed) 36.dp else 38.dp)
                            .then(
                                if (!isListView) Modifier.clickable(
                                    interactionSource = listInteraction,
                                    indication = null
                                ) { onViewTypeChanged() }
                                else Modifier
                            )
                    )
                    Image(
                        painter = painterResource(
                            if (showGridPressed) R.drawable.ic_grid_button_p
                            else R.drawable.ic_grid_button
                        ),
                        contentDescription = "썸네일 뷰",
                        modifier = Modifier
                            .size(if (showGridPressed) 36.dp else 38.dp)
                            .then(
                                if (isListView) Modifier.clickable(
                                    interactionSource = gridInteraction,
                                    indication = null
                                ) { onViewTypeChanged() }
                                else Modifier
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleSort() }
                ) {
                    Text(
                        if (uiState.sortDescending) "최신순" else "오래된순",
                        style = DungGeunMoSubtitle,
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = if (uiState.sortDescending) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "정렬",
                        tint = TextPrimary,
                        modifier = Modifier.padding(start = 2.dp).size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onSearchClick() },
                    painter = painterResource(R.drawable.search),
                    contentDescription = "내 책 검색"
                )
            }

            when (uiState.viewType) {
                HistoryViewType.DATASET -> {
                    val gridState = rememberLazyGridState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = gridState.layoutInfo.totalItemsCount
                            last >= total - 2 && total > 0 && uiState.nowPage < uiState.totalPages - 1
                        }
                    }
                    LaunchedEffect(shouldLoadMore.value) { if (shouldLoadMore.value) onLoadMore() }
                    val bookCount = uiState.books.size
                    val emptyCount = if (bookCount < 12) {
                        12 - bookCount
                    } else {
                        (3 - bookCount % 3) % 3
                    }
                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier
                            .padding(horizontal = 20.dp),
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            count = bookCount,
                            key = { uiState.books[it].mybookId }
                        ) { index ->
                            val book = uiState.books[index]
                            HistoryDataSetBookItem(
                                book = book,
                                onClick = { onBookClick(book.mybookId) },
                                modifier = Modifier.animateItem()
                            )
                        }
                        items(count = emptyCount) {
                            Box(modifier = Modifier.height(140.dp).padding(end = 1.dp, bottom = 1.dp)) {
                                PixelShadowBox(
                                    modifier = Modifier.fillMaxSize(),
                                    backgroundColor = Color(0xFFD4D4D4),
                                ) {}
                            }
                        }
                    }
                }
                HistoryViewType.LIST -> {
                    val listState = rememberLazyListState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = listState.layoutInfo.totalItemsCount
                            last >= total - 2 && total > 0 && uiState.nowPage < uiState.totalPages - 1
                        }
                    }
                    LaunchedEffect(shouldLoadMore.value) { if (shouldLoadMore.value) onLoadMore() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .padding(top = 30.dp)
                    ) {
                        // Table header with retro shadow
                        PixelShadowBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            backgroundColor = Color(0xFFD4D4D4),
                            showBorder = true,
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                                    text = "START",
                                    style = DungGeunMoBody.copy(letterSpacing = 3.2.sp),
                                    color = TextPrimary
                                )
                                Text(
                                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                                    text = "FINISH",
                                    style = DungGeunMoBody.copy(letterSpacing = 3.2.sp),
                                    color = TextPrimary
                                )
                                Text(
                                    modifier = Modifier.weight(2f).padding(start = 10.dp),
                                    text = "BOOK NAME",
                                    style = DungGeunMoBody.copy(letterSpacing = 3.2.sp),
                                    color = TextPrimary
                                )
                            }
                        }
                        LazyColumn(state = listState) {
                            if (uiState.books.isEmpty()) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(36.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.weight(2f))
                                        Text(
                                            modifier = Modifier
                                                .weight(2f)
                                                .padding(start = 10.dp),
                                            text = "읽고 있는 책을 추가해주세요.",
                                            style = WantedSansBodySmall,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                            items(
                                count = uiState.books.size,
                                key = { uiState.books[it].mybookId }
                            ) { index ->
                                val book = uiState.books[index]
                                HistoryListBookItem(
                                    book = book,
                                    isOdd = index % 2 == 0,
                                    onClick = { onBookClick(book.mybookId) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun HistoryListBookItem(book: HistoryBookInfo, isOdd: Boolean = false, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val startDateFormatted = book.startedDate.take(10).replace("-", "").drop(2)
    val finishDateFormatted = book.finishedDate?.take(10)?.replace("-", "")?.drop(2) ?: "-"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (isOdd) BackgroundDefault else Color.White)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(start = 10.dp),
            text = startDateFormatted,
            style = WantedSansBodySmall,
            color = TextPrimary
        )
        Text(
            modifier = Modifier.weight(1f).padding(start = 10.dp),
            text = finishDateFormatted,
            style = WantedSansBodySmall,
            color = TextPrimary
        )
        Text(
            modifier = Modifier.weight(2f).padding(start = 10.dp, end = 8.dp),
            text = book.title,
            style = WantedSansBodySmall,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HistoryDataSetBookItem(book: HistoryBookInfo, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(140.dp).padding(end = 1.dp, bottom = 1.dp)) {
        PixelShadowButton(
            onClick = onClick,
            backgroundColor = Color(0xFFD4D4D4),
            modifier = Modifier.fillMaxSize(),
        ) {
            AsyncImage(
                model = book.coverImage,
                contentDescription = book.title,
                modifier = Modifier
                    .width(75.dp)
                    .height(105.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private val dummyBooks = listOf(
    HistoryBookInfo(mybookId = 1, title = "소년과 두더지와 여우와 말", author = listOf("찰리 맥커시"), coverImage = "", description = null, startedDate = "2026-01-15", finishedDate = "2026-02-10"),
    HistoryBookInfo(mybookId = 2, title = "달러구트 꿈 백화점", author = listOf("이미예"), coverImage = "", description = null, startedDate = "2026-02-01", finishedDate = null),
    HistoryBookInfo(mybookId = 3, title = "역행자", author = listOf("자청"), coverImage = "", description = null, startedDate = "2025-12-20", finishedDate = "2026-01-05"),
    HistoryBookInfo(mybookId = 4, title = "아몬드", author = listOf("손원평"), coverImage = "", description = null, startedDate = "2025-11-10", finishedDate = "2025-12-01"),
    HistoryBookInfo(mybookId = 5, title = "불편한 편의점", author = listOf("김호연"), coverImage = "", description = null, startedDate = "2025-10-01", finishedDate = "2025-10-20")
)

@Preview(showBackground = true)
@Composable
fun HistoryScreenListPreview() {
    IkdamanTheme {
        HistoryScreenUI(
            uiState = HistoryBookState(
                isLoading = false,
                viewType = HistoryViewType.LIST,
                books = dummyBooks
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenGridPreview() {
    IkdamanTheme {
        HistoryScreenUI(
            uiState = HistoryBookState(
                isLoading = false,
                viewType = HistoryViewType.DATASET,
                books = dummyBooks
            )
        )
    }
}
