package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.domain.model.StoreBookItem
import project.side.ui.R
import project.side.ui.component.HomeBookItem
import project.side.ui.component.PixelShadowButton
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.DungGeunMoEtc
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    nickname: String = "",
    storeBooks: List<StoreBookItem> = emptyList(),
    sortDescending: Boolean = true,
    onToggleSort: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onBookClick: (Int) -> Unit = {},
    onStartReading: (Int, String) -> Unit = { _, _ -> },
    onDelete: (Int) -> Unit = {},
    navigateToSetting: () -> Unit = {},
    navigateToSearchBook: () -> Unit = {},
    navigateToMyBookSearch: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember(storeBooks.size) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 2 && totalItems > 0 && storeBooks.size >= 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
            .padding(horizontal = 16.dp)
    ) {
        item {
            HomeHeader(
                navigateToSetting = navigateToSetting,
                navigateToSearchBook = navigateToSearchBook
            )
        }

        if (storeBooks.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(60.dp))
                Image(
                    painter = painterResource(R.drawable.default_image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navigateToSearchBook() },
                    contentScale = ContentScale.FillWidth
                )
            }
        } else {
            item {
                // Sort bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleSort() }
                    ) {
                        Text(
                            text = if (sortDescending) "최신순" else "오래된순",
                            style = DungGeunMoSubtitle,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = if (sortDescending) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "정렬",
                            tint = TextPrimary,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { navigateToMyBookSearch() },
                        painter = painterResource(R.drawable.search),
                        contentDescription = "내 책 검색"
                    )
                }
            }

            items(
                count = storeBooks.size,
                key = { storeBooks[it].mybookId }
            ) { index ->
                val book = storeBooks[index]
                Column(modifier = Modifier.animateItem()) {
                    HomeBookItem(
                        index = if (sortDescending) storeBooks.size - index else index + 1,
                        title = book.title,
                        coverImage = book.coverImage,
                        date = book.createdDate.take(10).replace("-", "."),
                        description = book.reason,
                        onClick = { onBookClick(book.mybookId) },
                        onStartReading = { onStartReading(book.mybookId, book.title) },
                        onDelete = { onDelete(book.mybookId) }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    navigateToSetting: () -> Unit = {},
    navigateToSearchBook: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Settings icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                PixelShadowButton(
                    onClick = { navigateToSetting() },
                    modifier = Modifier.size(30.dp),
                    backgroundColor = BackgroundGray,
                    shadowOffset = 1.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "설정",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Title
        Text(
            text = "지금 떠오르는\n책이 있나요...!\n",
            style = DungGeunMoHomeTitle,
            color = TextPrimary,
            modifier = Modifier.padding(top = 20.dp)
        )

        // [+] ADD BOOK button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.End
        ) {
            PixelShadowButton(
                onClick = { navigateToSearchBook() },
                backgroundColor = BackgroundGray
            ) {
                Text(
                    text = "[+] ADD BOOK",
                    style = DungGeunMoEtc,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    IkdamanTheme {
        HomeScreen()
    }
}
