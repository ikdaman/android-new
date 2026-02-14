package project.side.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import project.side.domain.model.MyBookSearchItem
import project.side.presentation.viewmodel.MyBookSearchViewModel
import project.side.ui.R
import project.side.ui.theme.InputBackground
import project.side.ui.theme.TagHistory
import project.side.ui.theme.TagStore
import project.side.ui.theme.TextGray
import project.side.ui.theme.Typography

@Composable
fun MyBookSearchScreen(
    viewModel: MyBookSearchViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onBookClick: (Int) -> Unit = {}
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val shouldLoadMore = remember(searchResults.size) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 2 && totalItems > 0 && searchResults.size >= 10
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMore()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(
            title = "내 책 검색",
            showBackButton = true,
            onBackButtonClicked = onBack
        )

        // Search input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.search(searchText) }
                ),
                textStyle = Typography.bodyMedium,
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = "검색어를 입력하세요",
                            style = Typography.bodyMedium.copy(color = TextGray)
                        )
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.search(searchText) }) {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "검색",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Search results
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(searchResults.size) { index ->
                    val item = searchResults[index]
                    MyBookSearchResultItem(
                        item = item,
                        onClick = { onBookClick(item.mybookId) }
                    )
                    if (index < searchResults.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyBookSearchResultItem(
    item: MyBookSearchItem,
    onClick: () -> Unit = {}
) {
    val isTodo = item.readingStatus == "TODO"
    val tag = if (isTodo) "내 서점" else "히스토리"
    val dateText = if (isTodo) {
        item.createdDate.take(10)
    } else {
        val start = item.startedDate?.take(10) ?: ""
        val end = item.finishedDate?.take(10) ?: ""
        if (start.isNotEmpty()) "$start ~ $end" else ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        // Book thumbnail
        if (item.coverImage != null) {
            AsyncImage(
                model = item.coverImage,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp, 112.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp, 112.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Tag
            Text(
                text = "[$tag]",
                style = Typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (isTodo) TagStore else TagHistory
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Date
            if (dateText.isNotEmpty()) {
                Text(
                    text = dateText,
                    style = Typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 16.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Title
            Text(
                text = item.title,
                style = Typography.titleLarge.copy(fontSize = 14.sp, lineHeight = 18.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Author
            Text(
                text = item.author.joinToString(", "),
                style = Typography.titleMedium.copy(fontSize = 11.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
