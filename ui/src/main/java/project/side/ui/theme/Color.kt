package project.side.ui.theme

import androidx.compose.ui.graphics.Color

// Primary
val Primary = Color(0xFF010196)

// Text
val TextPrimary = Color(0xFF333333)
val TextSecondary = Color(0xFFD4D4D4)
val TextWhite = Color(0xFFFFFFFF)
val TextHint = Color(0xFFD4D4D4)

// Background
val BackgroundDefault = Color(0xFFEBEEF3)
val BackgroundWhite = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFD4D4D4)
val BackgroundDark = Color(0xFF515151)
val InputBackground = Color(0xFFF7F6EF)

// Divider / Border
val DividerGray = Color(0xFFF5F5F5)
val BorderBlack = Color(0xFF333333)

// Surface
val SurfaceGray = Color(0xFFEEEEEE)
val TextGray = Color(0xFF999999)

// Toast
val ToastBackground = Color(0xFF515151)

// Reading status (CC-1: 읽고 싶은 책 / 읽는 중 / 완독)
// 픽셀/레트로 컨셉 친화. 모두 0xFF 불투명 + 어둠 테두리(BorderBlack)와 함께 사용
val StatusWish = Color(0xFFE8F0FF)     // 파랑 tint - 읽고 싶은 책
val StatusReading = Color(0xFFFFF3D6)  // 주황 tint - 읽는 중
val StatusDone = Color(0xFFD6FAE8)     // 초록 tint - 완독

// Destructive action (삭제·탈퇴 등 파괴적 액션)
val DangerAccent = Color(0xFFE24646)   // 레트로 CRT 적색
