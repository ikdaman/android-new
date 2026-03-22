package project.side.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import project.side.ui.R
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoHeader
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary

@Composable
fun TitleBar(
    title: String = "",
    showBackButton: Boolean = false,
    onBackButtonClicked: () -> Unit = {},
    rightText: String? = null,
    onRightClick: (() -> Unit)? = null,
    rightText2: String? = null,
    onRightClick2: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDefault)
            .height(58.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button (left)
        if (showBackButton) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
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

        // Title (center)
        Text(
            text = title,
            style = DungGeunMoHeader,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Right actions
        if (rightText != null || rightText2 != null) {
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                if (rightText2 != null) {
                    Text(
                        text = rightText2,
                        style = DungGeunMoBody,
                        color = TextPrimary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onRightClick2?.invoke() }
                    )
                }
                if (rightText != null) {
                    Text(
                        text = rightText,
                        style = DungGeunMoBody,
                        color = TextPrimary,
                        modifier = Modifier.clickable { onRightClick?.invoke() }
                    )
                }
            }
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

@Preview(showBackground = true)
@Composable
fun TitleBarWithActionPreview() {
    IkdamanTheme {
        TitleBar(
            title = "책 추가하기",
            showBackButton = true,
            onBackButtonClicked = {},
            rightText = "저장",
            onRightClick = {}
        )
    }
}
