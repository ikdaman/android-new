package project.side.widget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUpdater
import project.side.widget.theme.ColorVariant

@AndroidEntryPoint
class WidgetConfigurationActivity : ComponentActivity() {

    @Inject lateinit var prefs: WidgetPreferences
    @Inject lateinit var updater: WidgetUpdater

    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish(); return
        }

        setContent {
            MaterialTheme {
                ConfigurationScreen(
                    onConfirm = { variant ->
                        lifecycleScope.launch {
                            prefs.setColor(appWidgetId, variant)
                            updater.refreshAll()
                            val resultValue = Intent().putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId
                            )
                            setResult(Activity.RESULT_OK, resultValue)
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfigurationScreen(onConfirm: (ColorVariant) -> Unit) {
    var selected by remember { mutableStateOf(ColorVariant.WHITE) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("배경 색을 선택해 주세요", fontSize = 18.sp)
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            VariantCard(
                label = "흰색",
                background = Color(0xFFF6F9FF),
                textColor = Color(0xFF333333),
                selected = selected == ColorVariant.WHITE,
                onClick = { selected = ColorVariant.WHITE },
            )
            VariantCard(
                label = "파란색",
                background = Color(0xFF010196),
                textColor = Color.White,
                selected = selected == ColorVariant.BLUE,
                onClick = { selected = ColorVariant.BLUE },
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onConfirm(selected) }, modifier = Modifier.fillMaxWidth()) {
            Text("완료")
        }
    }
}

@Composable
private fun VariantCard(
    label: String,
    background: Color,
    textColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 120.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color(0xFF010196) else Color.LightGray,
                shape = RoundedCornerShape(22.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = textColor, fontSize = 16.sp)
    }
}
