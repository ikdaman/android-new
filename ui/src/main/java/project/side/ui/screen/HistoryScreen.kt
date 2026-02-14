package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import project.side.domain.model.HistoryBookInfo
import project.side.presentation.model.HistoryBookState
import project.side.presentation.model.HistoryViewType
import project.side.presentation.viewmodel.HistoryViewModel
import project.side.ui.R
import project.side.ui.component.TitleBar
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBookClick: (Int) -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    HistoryScreenUI(
        uiState = uiState,
        onViewTypeChanged = viewModel::onViewTypeChanged,
        onLoadMore = { viewModel.loadMore() },
        onRetry = { viewModel.getBooks() },
        onBookClick = onBookClick
    )
}

@Composable
fun HistoryScreenUI(
    uiState: HistoryBookState = HistoryBookState(),
    onViewTypeChanged: () -> Unit = {},
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
                Text(
                    text = uiState.errorMessage ?: "",
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.TextButton(onClick = onRetry) {
                    Text("다시 시도", style = Typography.labelLarge)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            TitleBar("히스토리")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("최신 순 (${uiState.books.size}권)", style = Typography.bodyMedium.copy(fontSize = 16.sp))
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(20.dp)
                        .clickable {
                            onViewTypeChanged()
                        },
                    painter = painterResource(
                        if (uiState.viewType == HistoryViewType.DATASET) {
                            R.drawable.list_view
                        } else {
                            R.drawable.dataset_view
                        }
                    ),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { },
                    painter = painterResource(R.drawable.search),
                    contentDescription = null
                )
            }

            when (uiState.viewType) {
                HistoryViewType.DATASET -> {
                    val gridState = rememberLazyGridState()
                    val shouldLoadMoreGrid = remember {
                        derivedStateOf {
                            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val totalItems = gridState.layoutInfo.totalItemsCount
                            lastVisibleItem >= totalItems - 2 && totalItems > 0
                        }
                    }
                    LaunchedEffect(shouldLoadMoreGrid.value) {
                        if (shouldLoadMoreGrid.value) onLoadMore()
                    }
                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier
                            .background(Color(0xFFEDEDED))
                            .padding(12.dp),
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.books.size) { index ->
                            val book = uiState.books[index]
                            HistoryDataSetBookItem(
                                book = book,
                                onClick = { onBookClick(book.mybookId) }
                            )
                        }
                    }
                }

                HistoryViewType.LIST -> {
                    val listState = rememberLazyListState()
                    val shouldLoadMoreList = remember {
                        derivedStateOf {
                            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val totalItems = listState.layoutInfo.totalItemsCount
                            lastVisibleItem >= totalItems - 2 && totalItems > 0
                        }
                    }
                    LaunchedEffect(shouldLoadMoreList.value) {
                        if (shouldLoadMoreList.value) onLoadMore()
                    }
                    LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .background(Color.Black),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = "START",
                                    style = Typography.bodyMedium.copy(color = Color.White),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = "FINISH",
                                    style = Typography.bodyMedium.copy(color = Color.White),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    modifier = Modifier.weight(2f),
                                    text = "BOOK NAME",
                                    style = Typography.bodyMedium.copy(color = Color.White),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        items(uiState.books.size) { index ->
                            val book = uiState.books[index]
                            HistoryListBookItem(
                                book = book,
                                onClick = { onBookClick(book.mybookId) }
                            )
                            HorizontalDivider(
                                color = Color.Black,
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun HistoryListBookItem(book: HistoryBookInfo, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(41.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalDivider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxHeight()
        )
        Text(
            modifier = Modifier.weight(1f),
            text = book.startedDate,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f),
            text = book.finishedDate ?: "-",
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(2f),
            text = book.title,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        VerticalDivider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun HistoryDataSetBookItem(book: HistoryBookInfo, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .background(Color(0xFFD9D9D9))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = book.coverImage,
            contentDescription = book.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    IkdamanTheme {
        HistoryScreenUI()
    }
}
