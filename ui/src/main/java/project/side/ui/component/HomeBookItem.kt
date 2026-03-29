package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall
import project.side.ui.theme.WantedSansBookTitle

@Composable
fun HomeBookItem(
    index: Int = 0,
    title: String = "",
    author: String = "",
    coverImage: String? = null,
    date: String = "",
    description: String? = null,
    onClick: () -> Unit = {},
    onStartReading: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    PixelShadowButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = BackgroundWhite,
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(BackgroundGray)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NO.${index} ($date)",
                    style = DungGeunMoSubtitle,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "독서 시작",
                    style = DungGeunMoSubtitle,
                    color = TextPrimary,
                    modifier = Modifier.clickable { onStartReading() }
                )
                Text(
                    text = " ㅣ ",
                    style = DungGeunMoSubtitle,
                    color = TextPrimary
                )
                Text(
                    text = "삭제",
                    style = DungGeunMoSubtitle,
                    color = TextPrimary,
                    modifier = Modifier.clickable { onDelete() }
                )
            }

            // Separator
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderBlack))

            // Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite)
                    .padding(top = 25.dp, bottom = 25.dp)
            ) {
                // Book cover
                Box(
                    modifier = Modifier.width(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (coverImage != null) {
                        AsyncImage(
                            model = coverImage,
                            contentDescription = title,
                            modifier = Modifier
                                .width(81.dp)
                                .height(114.dp)
                                .border(1.dp, BorderBlack),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(81.dp)
                                .height(114.dp)
                                .background(Color.LightGray)
                                .border(1.dp, BorderBlack)
                        )
                    }
                }

                // Book info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    Text(
                        text = title,
                        style = WantedSansBookTitle,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(9.dp))
                    if (!description.isNullOrEmpty()) {
                        Text(
                            text = description,
                            style = WantedSansBodySmall,
                            color = TextPrimary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "이 책을 읽고 싶은 이유는 무엇인가요?",
                            style = WantedSansBodySmall,
                            color = TextPrimary.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBookItemPreview() {
    IkdamanTheme {
        HomeBookItem(
            index = 12,
            title = "UX/UI 디자인 완벽 가이드: IA와 유저 플로우를 이해할 수 있는 방법",
            date = "2025.11.24",
            description = "팀장님이 추천해 준 책이라서 꼽아봤는데 이러쿵 저러쿵"
        )
    }
}
