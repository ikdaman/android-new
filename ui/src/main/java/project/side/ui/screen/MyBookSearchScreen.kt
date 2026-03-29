package project.side.ui.screen

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import project.side.ui.component.RetroLoadingScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.TitleBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import project.side.domain.model.MyBookSearchItem
import project.side.presentation.viewmodel.MyBookSearchViewModel
import project.side.ui.R
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.Primary
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextWhite
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.theme.WantedSansBookTitle
import project.side.ui.util.rememberOneClickHandler

@Composable
fun MyBookSearchScreen(
    viewModel: MyBookSearchViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onBookClick: (Int) -> Unit = {}
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val oneClickHandler = rememberOneClickHandler()

    val listState = rememberLazyListState()
    val shouldLoadMore = remember(searchResults.size) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 2 && totalItems > 0 && searchResults.size >= 10
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) viewModel.loadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
    ) {
        TitleBar(
            title = "내 책 검색",
            showBackButton = true,
            onBackButtonClicked = { oneClickHandler { onBack() } }
        )

        // Search input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                        singleLine = true,
                        textStyle = DungGeunMoBody.copy(color = TextPrimary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.search(searchText) }),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text("검색어를 입력하세요", style = DungGeunMoBody, color = TextHint)
                            }
                            innerTextField()
                        }
                    )
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.search(searchText) }
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "검색",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        RetroLoadingScreen(isLoading = isLoading) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(searchResults.size) { index ->
                    val item = searchResults[index]
                    MyBookSearchResultItem(item = item, onClick = { onBookClick(item.mybookId) })
                    if (index < searchResults.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyBookSearchResultItem(item: MyBookSearchItem, onClick: () -> Unit = {}) {
    val (tag, tagColor) = when (item.readingStatus) {
        "TODO" -> "읽다만" to Primary
        "COMPLETED" -> "완독" to TextPrimary
        else -> "읽는 중" to TextPrimary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        if (item.coverImage != null) {
            AsyncImage(
                model = item.coverImage,
                contentDescription = item.title,
                modifier = Modifier.size(80.dp, 112.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp, 112.dp)
                    .background(BackgroundWhite)
                    .border(1.dp, BorderBlack)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(tagColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tag,
                    style = DungGeunMoTag,
                    color = TextWhite
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.title,
                style = WantedSansBookTitle,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.author.joinToString(", "),
                style = WantedSansBodySmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
