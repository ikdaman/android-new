package project.side.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import project.side.ui.component.BookRegisterBottomSheet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import project.side.domain.model.BookItem
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.ui.component.CustomSnackbarHost
// saveState is Boolean? exposed from ViewModel
import project.side.ui.theme.IkdamanTheme

@Composable
fun AddBookScreen(
    appNavController: NavController,
    viewModel: SearchBookViewModel?,
    selectedBook: BookItem? = null
) {
    // clear searched book once when this screen enters composition
    LaunchedEffect(viewModel) {
        viewModel?.clearSearchedBook()
    }

    // prefer an explicit selectedBook param if provided, otherwise read from viewModel's stateflow
    val selectedBookResolved: BookItem = selectedBook
        ?: viewModel?.selectedBookItem?.collectAsState()?.value ?: BookItem()

    val scrollState = rememberScrollState()
    // shared show state for BookRegisterBottomSheet
    val showRegister = remember { mutableStateOf(false) }

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        appNavController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                        )
                    }
                    Text(
                        "책 추가하기",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = { showRegister.value = true }) {
                        Text("저장", color = Color.Black)
                    }
                }
                Spacer(Modifier.height(16.dp))

                AsyncImage(
                    model = selectedBookResolved.cover,
                    contentDescription = "book cover",
                    modifier = Modifier
                        .width(131.dp)
                        .height(181.dp),
                    placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                    fallback = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                )

                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(text = "제목", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.title, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "작가", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.author, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "출판사", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.publisher, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "책 소개", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.description, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "페이지 수", style = MaterialTheme.typography.labelMedium)
                    Text(text = (selectedBookResolved.subInfo?.itemPage ?: "") + "p", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "출간일", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.pubDate, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(22.dp))
                    Text(text = "ISBN", style = MaterialTheme.typography.labelMedium)
                    Text(text = selectedBookResolved.isbn, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(24.dp))
                // observe save result from viewModel and close screen on success
                if (viewModel != null) {
                    val saveState by viewModel.saveState.collectAsState(initial = null)
                    LaunchedEffect(saveState) {
                        when (saveState) {
                            true -> {
                                // show success message and navigate back
                                snackbarHostState.showSnackbar("책을 저장했어요")
                                appNavController.popBackStack()
                            }
                            false -> {
                                // show failure message and stay on screen
                                snackbarHostState.showSnackbar("책 저장에 실패했어요")
                            }
                            else -> Unit
                        }
                    }
                }

                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text("알라딘에서 보기", style = MaterialTheme.typography.labelMedium)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
                BookRegisterBottomSheet(
                    show = showRegister.value,
                    onDismiss = { showRegister.value = false },
                    onConfirm = { reason, startDate, endDate ->
                        // trigger save with optional reason/startDate/endDate
                        showRegister.value = false
                        viewModel?.saveSelectedBook(reason, startDate, endDate)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBookScreenPreview() {
    IkdamanTheme {
        AddBookScreen(
            rememberNavController(),
            viewModel = null,
            selectedBook = BookItem(
                title = "책 제목",
                link = "https://picsum.photos/200/300",
                author = "작가",
                isbn = "1234567890",
                description = "책 소개",
                pubDate = "2026-01-29"
            )
        )
    }
}
