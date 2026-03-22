package project.side.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import project.side.ui.R

val DungGeunMo = FontFamily(
    Font(R.font.dunggeunmo, FontWeight.Normal)
)

val WantedSans = FontFamily(
    Font(R.font.wanted_sans_regular, FontWeight.Normal),
    Font(R.font.wanted_sans_semibold, FontWeight.SemiBold)
)

// Figma Typography tokens
// DungGeunMo styles
val DungGeunMoHomeTitle = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 28.sp,
    lineHeight = 48.sp
)

val DungGeunMoHeader = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 18.sp
)

val DungGeunMoPopupTitle = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp
)

val DungGeunMoBody = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 16.sp
)

val DungGeunMoSubtitle = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 14.sp
)

val DungGeunMoTag = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.8.sp
)

val DungGeunMoEtc = TextStyle(
    fontFamily = DungGeunMo,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 20.sp
)

// Wanted Sans styles
val WantedSansBookTitle = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 18.sp
)

val WantedSansBookTitleLarge = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp
)

val WantedSansSubtitleBold = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 18.sp
)

val WantedSansBody = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 22.4.sp
)

val WantedSansBodySmall = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

val WantedSansCaption = TextStyle(
    fontFamily = WantedSans,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp
)

// Material3 Typography mapping
val Typography = Typography(
    // DungGeunMo mappings
    displayLarge = DungGeunMoHomeTitle,
    headlineLarge = DungGeunMoHeader,
    headlineMedium = DungGeunMoPopupTitle,

    // Wanted Sans mappings
    titleLarge = WantedSansBookTitleLarge,
    titleMedium = WantedSansBookTitle,
    titleSmall = WantedSansSubtitleBold,

    bodyLarge = WantedSansBody,
    bodyMedium = WantedSansBodySmall,
    bodySmall = WantedSansCaption,

    // DungGeunMo for labels (buttons, nav, tags)
    labelLarge = DungGeunMoBody,
    labelMedium = DungGeunMoSubtitle,
    labelSmall = DungGeunMoTag
)
