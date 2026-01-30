package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import project.side.ui.theme.IkdamanTheme

@Composable
fun ManualBookInputScreen(
    appNavController: NavController,
    onSave: (title: String, author: String, publisher: String, pubDate: String, isbn: String, pageCount: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var pubDate by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
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
                        "직접 입력",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = {
                        onSave(title, author, publisher, pubDate, isbn, pageCount)
                    }) {
                        Text("저장", color = Color.Black)
                    }
                }
                Spacer(Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // 제목
                    Text(
                        text = "제목",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "책 제목을 입력하세요"
                    )
                    Spacer(Modifier.height(22.dp))

                    // 작가
                    Text(
                        text = "작가",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = author,
                        onValueChange = { author = it },
                        placeholder = "작가를 입력하세요"
                    )
                    Spacer(Modifier.height(22.dp))

                    // 출판사
                    Text(
                        text = "출판사",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        placeholder = "출판사를 입력하세요"
                    )
                    Spacer(Modifier.height(22.dp))

                    // 출간일
                    Text(
                        text = "출간일",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = pubDate,
                        onValueChange = { pubDate = it },
                        placeholder = "YYYY-MM-DD",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(22.dp))

                    // ISBN
                    Text(
                        text = "ISBN",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = isbn,
                        onValueChange = { isbn = it },
                        placeholder = "ISBN을 입력하세요",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(22.dp))

                    // 페이지 수
                    Text(
                        text = "페이지 수",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BookInputField(
                        value = pageCount,
                        onValueChange = { pageCount = it },
                        placeholder = "페이지 수를 입력하세요",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
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
            appNavController = rememberNavController()
        )
    }
}
