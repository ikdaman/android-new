package project.side.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import project.side.domain.model.BookItem
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.ui.MAIN_ROUTE
import project.side.ui.component.BookRegisterBottomSheet
import project.side.ui.component.CustomSnackbarHost
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.theme.WantedSansBookTitleLarge
import project.side.ui.util.rememberOneClickHandler

@Composable
fun AddBookScreen(
    appNavController: NavController,
    viewModel: SearchBookViewModel?,
    selectedBook: BookItem? = null
) {
    LaunchedEffect(viewModel) {
        viewModel?.clearSearchedBook()
    }

    val selectedBookResolved: BookItem = selectedBook
        ?: viewModel?.selectedBookItem?.collectAsState()?.value ?: BookItem()

    val scrollState = rememberScrollState()
    val showRegister = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val oneClickHandler = rememberOneClickHandler()

    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDefault)
        ) {
            TitleBar(
                title = "책 추가하기",
                showBackButton = true,
                onBackButtonClicked = { oneClickHandler { appNavController.popBackStack() } },
                rightText = "저장",
                onRightClick = { showRegister.value = true }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                // Book cover
                AsyncImage(
                    model = selectedBookResolved.cover,
                    contentDescription = "book cover",
                    modifier = Modifier
                        .width(210.dp)
                        .height(158.dp),
                    placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                    fallback = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                )

                Spacer(Modifier.height(20.dp))

                // Title & Author
                Text(
                    text = selectedBookResolved.title,
                    style = WantedSansBookTitleLarge,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = selectedBookResolved.author,
                    style = WantedSansBody,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = selectedBookResolved.publisher,
                    style = WantedSansBody,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                // Book info fields
                InfoField("페이지 수", (selectedBookResolved.subInfo?.itemPage ?: "") + "p")
                Spacer(Modifier.height(20.dp))
                InfoField("출간일", selectedBookResolved.pubDate)
                Spacer(Modifier.height(20.dp))
                InfoField("ISBN", selectedBookResolved.isbn)
                Spacer(Modifier.height(20.dp))
                InfoField("책 소개", selectedBookResolved.description)

                Spacer(Modifier.height(20.dp))

                // Aladin link
                if (selectedBookResolved.itemId != 0L) {
                    val context = LocalContext.current
                    val aladinUrl = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=${selectedBookResolved.itemId}&partner=openAPI&start=api"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .background(BackgroundWhite)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(aladinUrl)))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "알라딘에서 더보기",
                            style = WantedSansBody,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // Save events
        if (viewModel != null) {
            LaunchedEffect(Unit) {
                viewModel.saveEvent.collect { event ->
                    when (event) {
                        is project.side.presentation.viewmodel.SaveEvent.Success -> {
                            appNavController.popBackStack(MAIN_ROUTE, inclusive = false)
                            project.side.presentation.util.SnackbarManager.show("책을 저장했어요")
                        }
                        is project.side.presentation.viewmodel.SaveEvent.Error -> {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    }
                }
            }
        }

        BookRegisterBottomSheet(
            show = showRegister.value,
            onDismiss = { showRegister.value = false },
            onConfirm = { reason, startDate, endDate ->
                showRegister.value = false
                viewModel?.saveSelectedBook(reason, startDate, endDate)
            }
        )
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = DungGeunMoSubtitle,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                style = WantedSansBody,
                color = TextPrimary
            )
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
                author = "작가",
                isbn = "1234567890",
                description = "책 소개",
                pubDate = "2026-01-29"
            )
        )
    }
}
