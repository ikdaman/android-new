package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import project.side.domain.model.BookItem
import project.side.domain.model.DomainResult
import project.side.presentation.model.SearchBookState
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.ui.BARCODE_ROUTE
import project.side.ui.R
import project.side.ui.theme.IkdamanTheme
import project.side.ui.util.noEffectClick
import project.side.ui.util.oneClick

@Composable
fun SearchBookScreen(
    appNavController: NavController? = null,
    onNavigateToAddBookScreen: () -> Unit = {},
    onNavigateToManualInputScreen: () -> Unit = {},
    viewModel: SearchBookViewModel? = hiltViewModel(),
    state: DomainResult<List<BookItem>>? = null
) {
    val searchState = viewModel?.searchState?.collectAsStateWithLifecycle()?.value ?: SearchBookState()
    val searchResult = viewModel?.bookDetail?.collectAsStateWithLifecycle()?.value

    LaunchedEffect(searchResult) {
        when (searchResult) {
            is DomainResult.Success -> {
                onNavigateToAddBookScreen()
            }
            else -> {}
        }
    }

    // Infinite scroll detection
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && searchState.hasMore && !searchState.isLoadingMore) {
            viewModel?.loadNextPage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            // Text Input Fields for Book Search
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .height(32.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    var text by remember { mutableStateOf("") }

                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = MaterialTheme.typography.labelMedium.copy(color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            viewModel?.searchBook(text)
                        }),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    "추가하고 싶은 책을 입력해주세요.",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray.copy(alpha = 0.6f))
                                )
                            }
                            innerTextField()
                        }
                    )

                    // camera icon opens barcode screen
                    Box(modifier = Modifier.oneClick {
                        appNavController?.navigate(BARCODE_ROUTE)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "camera",
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Box(modifier = Modifier.oneClick {
                        viewModel?.searchBook(text)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "search",
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            when {
                searchState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                searchState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = searchState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { onNavigateToManualInputScreen() }
                        ) {
                            Text(
                                text = "책 직접 입력하기",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                searchState.books.isNotEmpty() -> {
                    // Total count
                    if (searchState.totalBookCount > 0) {
                        Text(
                            text = "총 ${searchState.totalBookCount}건",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    LazyColumn(state = listState) {
                        items(searchState.books, key = { it.isbn.ifBlank { it.title + it.author } }) { book ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max)
                                    .padding(vertical = 16.dp)
                                    .noEffectClick {
                                        viewModel?.searchBookByIsbn(book.isbn)
                                    }) {
                                AsyncImage(
                                    model = book.cover,
                                    contentDescription = "book cover",
                                    modifier = Modifier
                                        .size(94.dp, 130.dp)
                                        .align(Alignment.CenterVertically),
                                    placeholder = ColorPainter(Color.Gray),
                                    error = ColorPainter(Color.Red)
                                )
                                Column(Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                                    Text(
                                        book.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(book.author, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        // Loading more indicator
                        if (searchState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBookScreenPreview() {
    IkdamanTheme {
        SearchBookScreen(
            viewModel = null,
            state = DomainResult.Success(
                listOf(
                    BookItem(
                        title = "책 제목1",
                        link = "https://picsum.photos/200/300",
                        author = "작가 1"
                    ),
                    BookItem(
                        title = "책 제목2",
                        link = "https://picsum.photos/200/300",
                        author = "작가 2"
                    ),
                )
            )
        )
    }
}
