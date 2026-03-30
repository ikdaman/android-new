package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import project.side.presentation.viewmodel.ManualInputViewModel
import project.side.ui.R
import project.side.ui.component.BookRegisterBottomSheet
import project.side.ui.component.CustomSnackbarHost
import project.side.ui.component.TitleBar
import project.side.ui.MAIN_ROUTE
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextHint
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.util.rememberOneClickHandler

@Composable
fun ManualBookInputScreen(
    appNavController: NavController,
    viewModel: ManualInputViewModel? = hiltViewModel<ManualInputViewModel>(),
    isLoggedIn: Boolean = true,
    onLoginRequired: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var pubDate by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }

    val showRegister = remember { mutableStateOf(false) }
    val pendingSave = remember { mutableStateOf(false) }
    val oneClickHandler = rememberOneClickHandler()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && pendingSave.value) {
            pendingSave.value = false
            if (title.isNotBlank() && author.isNotBlank()) {
                showRegister.value = true
            }
        }
    }

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
                title = "직접 입력",
                showBackButton = true,
                onBackButtonClicked = { oneClickHandler { appNavController.popBackStack() } },
                rightText = "저장",
                onRightClick = {
                    if (!isLoggedIn) {
                        pendingSave.value = true
                        onLoginRequired()
                    } else if (title.isNotBlank() && author.isNotBlank()) {
                        showRegister.value = true
                    }
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
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
                        viewModel?.saveManualBookInfoFromUi(
                            title = title,
                            author = author,
                            publisher = publisher.ifBlank { null },
                            pubDate = pubDate.ifBlank { null },
                            isbn = isbn.ifBlank { null },
                            pageCount = pageCount.ifBlank { null },
                            reason = reason,
                            startDate = startDate,
                            endDate = endDate
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.book_default),
                    contentDescription = "book cover",
                    modifier = Modifier
                        .width(131.dp)
                        .height(181.dp)
                )
                Spacer(Modifier.height(16.dp))

                // Input fields
                InputFieldWithLabel("제목", required = true) {
                    BookInputField(title, { title = it }, "책 제목을 입력하세요")
                }
                Spacer(Modifier.height(20.dp))

                InputFieldWithLabel("작가", required = true) {
                    BookInputField(author, { author = it }, "작가를 입력하세요")
                }
                Spacer(Modifier.height(20.dp))

                InputFieldWithLabel("출판사") {
                    BookInputField(publisher, { publisher = it }, "출판사를 입력하세요")
                }
                Spacer(Modifier.height(20.dp))

                InputFieldWithLabel("출간일") {
                    BookInputField(pubDate, { pubDate = it }, "YYYY-MM-DD", KeyboardType.Number)
                }
                Spacer(Modifier.height(20.dp))

                InputFieldWithLabel("ISBN") {
                    BookInputField(isbn, { isbn = it }, "ISBN을 입력하세요", KeyboardType.Number)
                }
                Spacer(Modifier.height(20.dp))

                InputFieldWithLabel("페이지 수") {
                    BookInputField(pageCount, { pageCount = it }, "페이지 수를 입력하세요", KeyboardType.Number)
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InputFieldWithLabel(
    label: String,
    required: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = label, style = DungGeunMoSubtitle, color = TextPrimary)
            if (required) {
                Spacer(Modifier.width(4.dp))
                Text(text = "필수", style = DungGeunMoTag, color = Primary)
            }
        }
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun BookInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = WantedSansBody,
                color = TextHint
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = WantedSansBody.copy(color = TextPrimary),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManualBookInputScreenPreview() {
    IkdamanTheme {
        ManualBookInputScreen(
            appNavController = rememberNavController(),
            viewModel = null
        )
    }
}
