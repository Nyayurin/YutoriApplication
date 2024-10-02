package cn.yurn.yutori.application.view.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AdaptiveNavigation(
    type: AdaptiveNavigationType,
    selected: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: AdaptiveNavigationColors = AdaptiveNavigationDefaults.colors(),
    content: AdaptiveNavigationScope.() -> Unit
) {
    val scope by run {
        val latestContent = rememberUpdatedState(content)
        remember {
            derivedStateOf {
                AdaptiveNavigationScope().apply(latestContent.value)
            }
        }
    }

    val defaultItemColors = AdaptiveNavigationDefaults.itemColors()

    when (type) {
        AdaptiveNavigationType.Bar -> {
            NavigationBar(
                modifier = modifier,
                containerColor = colors.container,
                contentColor = colors.content,
            ) {
                for (item in scope.items) {
                    NavigationBarItem(
                        selected = selected == item.index,
                        onClick = { onChange(item.index) },
                        icon = item.icon,
                        modifier = item.modifier,
                        label = item.label,
                        colors = item.colors?.bar ?: defaultItemColors.bar
                    )
                }
            }
        }

        AdaptiveNavigationType.Rail -> {
            NavigationRail(
                modifier = modifier,
                containerColor = colors.container,
                contentColor = colors.content,
            ) {
                Spacer(Modifier.weight(1f))
                for (item in scope.items) {
                    NavigationRailItem(
                        selected = selected == item.index,
                        onClick = { onChange(item.index) },
                        icon = item.icon,
                        modifier = item.modifier,
                        label = item.label,
                        colors = item.colors?.rail ?: defaultItemColors.rail
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

class AdaptiveNavigationScope {
    val items: MutableList<AdaptiveNavigationItem> = mutableListOf()

    fun item(
        index: Int,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        colors: AdaptiveNavigationItemColors? = null
    ) {
        items.add(
            AdaptiveNavigationItem(
                index = index,
                icon = icon,
                label = label,
                modifier = modifier,
                colors = colors
            )
        )
    }
}

class AdaptiveNavigationItem(
    val index: Int,
    val icon: @Composable () -> Unit,
    val label: @Composable () -> Unit,
    val modifier: Modifier,
    val colors: AdaptiveNavigationItemColors?
)

enum class AdaptiveNavigationType {
    Bar, Rail
}

class AdaptiveNavigationColors(
    val container: Color,
    val content: Color,
)

class AdaptiveNavigationItemColors(
    val bar: NavigationBarItemColors,
    val rail: NavigationRailItemColors
)

object AdaptiveNavigationDefaults {
    @Composable
    fun colors(
        container: Color = MaterialTheme.colorScheme.surfaceContainer,
        content: Color = contentColorFor(container),
    ) = AdaptiveNavigationColors(container = container, content = content)

    @Composable
    fun itemColors(
        bar: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
        rail: NavigationRailItemColors = NavigationRailItemDefaults.colors()
    ) = AdaptiveNavigationItemColors(bar = bar, rail = rail)
}