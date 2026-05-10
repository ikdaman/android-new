package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import project.side.widget.R

@Composable
fun BookHeartIcon(
    tint: Color,
    sizeDp: Int = 16,
    modifier: GlanceModifier = GlanceModifier,
) {
    Image(
        provider = ImageProvider(R.drawable.ic_book_heart),
        contentDescription = null,
        modifier = modifier.size(sizeDp.dp),
        colorFilter = ColorFilter.tint(ColorProvider(tint)),
    )
}
