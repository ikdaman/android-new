package project.side.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.side.ui.R
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

@Composable
fun TitleBar(
    title: String = "",
    showBackButton: Boolean = false,
    onBackButtonClicked: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onBackButtonClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow_left),
                        contentDescription = null,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(end = if (showBackButton) 30.dp else 0.dp)
                    .weight(1f),
                text = title,
                style = Typography.titleSmall.copy(fontSize = 24.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleBarPreview() {
    IkdamanTheme {
        TitleBar(title = "내 책 검색", showBackButton = true)
    }
}