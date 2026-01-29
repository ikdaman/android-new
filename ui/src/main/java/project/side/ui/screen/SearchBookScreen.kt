package project.side.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    viewModel: SearchBookViewModel? = hiltViewModel(),
    state: DomainResult<List<BookItem>>? = viewModel?.bookResultListState?.collectAsState()?.value
) {
    val searchResult = viewModel?.bookDetail?.collectAsStateWithLifecycle()?.value
    LaunchedEffect(searchResult) {
        when (searchResult) {
            is DomainResult.Success -> {
                onNavigateToAddBookScreen()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            Text(
                "책 추가하기",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                        })
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

            Column {
                if (state is DomainResult.Success) {
                    state.data.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .padding(vertical = 16.dp)
                                .noEffectClick {
                                    viewModel?.searchBookByIsbn(it.isbn)
                                }) {
                            AsyncImage(
                                model = it.cover,
                                contentDescription = "book cover",
                                modifier = Modifier
                                    .size(94.dp, 130.dp)
                                    .align(Alignment.CenterVertically),
                                placeholder = ColorPainter(Color.Gray),
                                error = ColorPainter(Color.Red)
                            )
                            Column(Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                                Text(
                                    it.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(it.author, style = MaterialTheme.typography.labelMedium)
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
