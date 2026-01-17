package project.side.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.side.ui.R
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

@Composable
fun HomeBookItem() {
    Row {
        Box(modifier = Modifier.height(200.dp)) {
            Box(
                modifier = Modifier
                    .size(134.dp, 188.dp)
                    .background(Color.LightGray)
            )
            Image(
                painter = painterResource(id = R.drawable.book_item_pin),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        Spacer(modifier = Modifier.width(22.dp))
        Column {
            Checkbox(
                modifier = Modifier.offset(x = (-12).dp),
                checked = false,
                onCheckedChange = {}
            )
            Text(
                "25.10.10",
                style = Typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 20.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "읽고 싶은 책으로 등록한 책의 제목이 최대 두줄까지 노출될 수 있습니다.",
                style = Typography.titleLarge.copy(fontSize = 12.sp, lineHeight = 16.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "작가 이름이 최대 한 줄까지 들어갑니다.",
                style = Typography.titleMedium.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "책을 읽고 싶어한 이유가 노출됩니다. 없다면, 책의 소개글이 노출되며 최대 세 줄까지 보여질 수 있습니다. 책을 읽고 싶어한 이...",
                style = Typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 16.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBookItemPreview() {
    IkdamanTheme {
        HomeBookItem()
    }
}
