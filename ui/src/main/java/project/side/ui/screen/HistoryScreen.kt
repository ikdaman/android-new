package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBookClick: (Int) -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    HistoryScreenUI(
        uiState = uiState,
        onViewTypeChanged = viewModel::onViewTypeChanged,
        onToggleSort = viewModel::toggleSort,
        onLoadMore = { viewModel.loadMore() },
        onRetry = { viewModel.getBooks() },
        onBookClick = onBookClick
    )
}

@Composable
fun HistoryScreenUI(
    uiState: HistoryBookState = HistoryBookState(),
    onViewTypeChanged: () -> Unit = {},
    onToggleSort: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onRetry: () -> Unit = {},
    onBookClick: (Int) -> Unit = {}
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.errorMessage != null) {
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
                Row {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isListView) Color(0xFFE4E4E4) else Color(0xFFD4D4D4))
                            .then(if (!isListView) Modifier.clickable { onViewTypeChanged() } else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(21.dp),
                            painter = painterResource(R.drawable.list_view),
                            contentDescription = "리스트 뷰"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (!isListView) Color(0xFFE4E4E4) else Color(0xFFD4D4D4))
                            .then(if (isListView) Modifier.clickable { onViewTypeChanged() } else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.dataset_view),
                            contentDescription = "썸네일 뷰"
                        )
                    }
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
            }

            when (uiState.viewType) {
                HistoryViewType.DATASET -> {
                    val gridState = rememberLazyGridState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = gridState.layoutInfo.totalItemsCount
                            last >= total - 2 && total > 0
                        }
                    }
                    LaunchedEffect(shouldLoadMore.value) { if (shouldLoadMore.value) onLoadMore() }
                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier
                            .padding(horizontal = 20.dp),
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.books.size) { index ->
                            val book = uiState.books[index]
                            HistoryDataSetBookItem(book = book, onClick = { onBookClick(book.mybookId) })
                        }
                    }
                }
                HistoryViewType.LIST -> {
                    val listState = rememberLazyListState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = listState.layoutInfo.totalItemsCount
                            last >= total - 2 && total > 0
                        }
                    }
                    LaunchedEffect(shouldLoadMore.value) { if (shouldLoadMore.value) onLoadMore() }
                    PixelShadowBox(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        backgroundColor = Color.White,
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .background(Color(0xFFD4D4D4)),
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
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderBlack))
                            // Table rows
                            LazyColumn(state = listState) {
                                items(uiState.books.size) { index ->
                                    val book = uiState.books[index]
                                    HistoryListBookItem(
                                        book = book,
                                        isEven = index % 2 == 0,
                                        onClick = { onBookClick(book.mybookId) }
                                    )
                                    HorizontalDivider(color = BorderBlack, thickness = 0.5.dp)
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
fun HistoryListBookItem(book: HistoryBookInfo, isEven: Boolean = false, onClick: () -> Unit = {}) {
    val startDateFormatted = book.startedDate.take(10).replace("-", "").drop(2)
    val finishDateFormatted = book.finishedDate?.take(10)?.replace("-", "")?.drop(2) ?: "-"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (isEven) Color.Transparent else Color.White)
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
fun HistoryDataSetBookItem(book: HistoryBookInfo, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .height(138.dp)
            .background(Color(0xFFD4D4D4))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
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

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    IkdamanTheme { HistoryScreenUI() }
}
