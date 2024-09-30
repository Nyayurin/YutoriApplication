package cn.yurn.yutori.application.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import cn.yurn.yutori.application.Setting
import org.jetbrains.compose.resources.Font
import yutoriapplication.application.generated.resources.MiSans_Regular
import yutoriapplication.application.generated.resources.Res

@Composable
actual fun platformColorScheme(): ColorScheme {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        when (Setting.darkMode ?: isSystemInDarkTheme()) {
            true -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    } else {
        when (Setting.darkMode ?: isSystemInDarkTheme()) {
            true -> darkScheme
            else -> lightScheme
        }
    }
}

@Composable
actual fun platformTypography(): Typography {
    val defaultTypography = MaterialTheme.typography
    var typography by remember { mutableStateOf<Typography?>(null) }
    val fontFamily = FontFamily(
        Font(Res.font.MiSans_Regular)
    )
    LaunchedEffect(Unit) {
        typography = Typography(
            defaultTypography.displayLarge.copy(fontFamily = fontFamily),
            defaultTypography.displayMedium.copy(fontFamily = fontFamily),
            defaultTypography.displaySmall.copy(fontFamily = fontFamily),
            defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
            defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
            defaultTypography.headlineSmall.copy(fontFamily = fontFamily),
            defaultTypography.titleLarge.copy(fontFamily = fontFamily),
            defaultTypography.titleMedium.copy(fontFamily = fontFamily),
            defaultTypography.titleSmall.copy(fontFamily = fontFamily),
            defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
            defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
            defaultTypography.bodySmall.copy(fontFamily = fontFamily),
            defaultTypography.labelLarge.copy(fontFamily = fontFamily),
            defaultTypography.labelMedium.copy(fontFamily = fontFamily),
            defaultTypography.labelSmall.copy(fontFamily = fontFamily)
        )
    }
    return typography ?: defaultTypography
}