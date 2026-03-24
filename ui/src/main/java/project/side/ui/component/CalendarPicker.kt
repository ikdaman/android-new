package project.side.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import project.side.ui.R
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansCaption
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarPicker(
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate ?: LocalDate.now())) }
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달",
                    tint = TextPrimary
                )
            }
            Text(
                text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                style = DungGeunMoPopupTitle,
                color = TextPrimary
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day of week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = WantedSansCaption,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val startOffset = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
        val daysInMonth = currentMonth.lengthOfMonth()
        val totalCells = startOffset + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(totalCells) { index ->
                if (index < startOffset) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    val day = index - startOffset + 1
                    val date = currentMonth.atDay(day)
                    val isSelected = date == selectedDate
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val showImage = isSelected || isPressed

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (showImage) {
                            Image(
                                painter = painterResource(id = R.drawable.pressed_date),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Text(
                            text = "$day",
                            style = WantedSansBody,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPickerPreview() {
    IkdamanTheme {
        CalendarPicker(selectedDate = LocalDate.now())
    }
}
