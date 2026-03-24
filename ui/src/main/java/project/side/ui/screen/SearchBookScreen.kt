package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import project.side.ui.component.PixelShadowBox
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.theme.WantedSansBookTitle
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
            is DomainResult.Success -> onNavigateToAddBookScreen()
            else -> {}
        }
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            // Search field
            PixelShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                backgroundColor = BackgroundWhite,
                shadowOffset = 2.dp,
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    var text by remember { mutableStateOf("") }

                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = DungGeunMoBody.copy(color = TextPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            viewModel?.searchBook(text)
                        }),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    "책 제목을 검색해주세요.",
                                    style = DungGeunMoBody,
                                    color = TextHint
                                )
                            }
                            innerTextField()
                        }
                    )

                    Box(modifier = Modifier.oneClick {
                        appNavController?.navigate(BARCODE_ROUTE)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "barcode",
                            tint = TextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Box(modifier = Modifier.oneClick {
                        viewModel?.searchBook(text)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "search",
                            tint = TextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                            style = DungGeunMoBody,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { onNavigateToManualInputScreen() }) {
                            Text(
                                text = "책 직접 입력하기",
                                style = DungGeunMoSubtitle
                            )
                        }
                    }
                }
                searchState.books.isNotEmpty() -> {
                    LazyColumn(state = listState) {
                        items(searchState.books, key = { it.isbn.ifBlank { it.title + it.author } }) { book ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(vertical = 10.dp)
                                    .noEffectClick {
                                        viewModel?.searchBookByIsbn(book.isbn)
                                    }
                            ) {
                                // Book cover
                                Box(
                                    modifier = Modifier.width(86.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = book.cover,
                                        contentDescription = "book cover",
                                        modifier = Modifier
                                            .size(84.dp, 100.dp),
                                        placeholder = ColorPainter(Color.Gray),
                                        error = ColorPainter(Color.Red)
                                    )
                                }
                                Column(
                                    Modifier.padding(start = 18.dp, top = 8.dp)
                                ) {
                                    Text(
                                        book.title,
                                        style = WantedSansBookTitle,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        book.author,
                                        style = WantedSansBodySmall,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        book.publisher,
                                        style = WantedSansBodySmall,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

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
                )
            )
        )
    }
}
