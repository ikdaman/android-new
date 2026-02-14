package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.side.domain.model.StoreBookItem
import project.side.ui.R
import project.side.ui.component.HomeBookItem
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

@Composable
fun HomeScreen(
    nickname: String = "",
    storeBooks: List<StoreBookItem> = emptyList(),
    onLoadMore: () -> Unit = {},
    onBookClick: (Int) -> Unit = {},
    navigateToSetting: () -> Unit = {},
    navigateToSearchBook: () -> Unit = {},
    navigateToMyBookSearch: () -> Unit = {},
) {
    val totalCount = storeBooks.size
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
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        item {
            HomeHeader(nickname, totalCount, navigateToSetting, navigateToSearchBook, navigateToMyBookSearch)
        }
        items(storeBooks.size) { index ->
            val book = storeBooks[index]
            HomeBookItem(
                title = book.title,
                author = book.author.joinToString(", "),
                coverImage = book.coverImage,
                date = book.createdDate,
                description = book.description,
                onClick = { onBookClick(book.mybookId) }
            )
            Spacer(modifier = Modifier.height(55.dp))
        }
    }

}

@Composable
fun HomeHeader(nickname: String = "", totalCount: Int = 0, navigateToSetting: () -> Unit = {}, navigateToBookInfo: () -> Unit = {}, navigateToMyBookSearch: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
                .size(24.dp)
                .align(Alignment.End)
                .clickable { navigateToSetting() },
            painter = painterResource(R.drawable.settings),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(bottom = 27.dp),
            text = "오늘 " + "${nickname.ifEmpty { "OO" }}님의\n" + "눈에 꽂힌 책은\n" + "무엇이었나요?",
            style = Typography.bodyLarge.copy(fontSize = 22.sp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7F6EF))
                .clickable { navigateToBookInfo() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(start = 9.dp, end = 10.dp),
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null
            )
            Text(text = "읽고 싶은 책 적어두기", style = Typography.bodyMedium)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(end = 8.dp), text = "최신 순")
            Text("(${totalCount}권)")
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(16.dp)
                    .clickable { navigateToMyBookSearch() },
                painter = painterResource(R.drawable.search),
                contentDescription = "내 책 검색",
            )
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