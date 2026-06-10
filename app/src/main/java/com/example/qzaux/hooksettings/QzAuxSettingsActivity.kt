package com.example.qzaux.hooksettings

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import com.example.qzaux.ui.theme.QzAuxTheme

class QzAuxSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QzAuxTheme {
                QzAuxSettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QzAuxSettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("qzaux_prefs", MODE_PRIVATE)

    var moduleEnabled by remember {
        mutableStateOf(prefs.getBoolean("module_enabled", true))
    }
    var debugMode by remember {
        mutableStateOf(prefs.getBoolean("debug_mode", false))
    }
    var logOutput by remember {
        mutableStateOf(prefs.getBoolean("log_output", true))
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Hook 设置") },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                SettingsGroup(
                    title = "模块开关",
                    items = listOf(
                        {
                            SettingsSwitchItem(
                                icon = Icons.Default.PowerSettingsNew,
                                title = "模块总开关",
                                subtitle = if (moduleEnabled) "已启用" else "已禁用",
                                checked = moduleEnabled,
                                onCheckedChange = { checked ->
                                    moduleEnabled = checked
                                    prefs.edit().putBoolean("module_enabled", checked).apply()
                                }
                            )
                        }
                    )
                )
            }

            item {
                SettingsGroup(
                    title = "调试",
                    items = listOf(
                        {
                            SettingsSwitchItem(
                                icon = Icons.Default.BugReport,
                                title = "调试模式",
                                subtitle = if (debugMode) "已开启" else "已关闭",
                                checked = debugMode,
                                onCheckedChange = { checked ->
                                    debugMode = checked
                                    prefs.edit().putBoolean("debug_mode", checked).apply()
                                }
                            )
                        },
                        {
                            SettingsSwitchItem(
                                icon = Icons.Default.Terminal,
                                title = "日志输出",
                                subtitle = if (logOutput) "已开启" else "已关闭",
                                checked = logOutput,
                                onCheckedChange = { checked ->
                                    logOutput = checked
                                    prefs.edit().putBoolean("log_output", checked).apply()
                                }
                            )
                        }
                    )
                )
            }

            item {
                SettingsGroup(
                    title = "高级",
                    items = listOf(
                        {
                            SettingsItemCell(
                                icon = Icons.Default.Settings,
                                title = "高级配置",
                                subtitle = "详细功能参数配置",
                                onClick = {
                                    val intent = Intent(context, QzAuxSettingsDetailActivity::class.java)
                                    context.startActivity(intent)
                                }
                            )
                        }
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    items: List<@Composable () -> Unit>,
) {
    if (items.isEmpty()) return

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        val cornerRadius = 24.dp
        val smallRadius = 4.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items.forEachIndexed { index, item ->
                val shape = when {
                    items.size == 1 -> RoundedCornerShape(cornerRadius)
                    index == 0 -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius, bottomStart = smallRadius, bottomEnd = smallRadius)
                    index == items.size - 1 -> RoundedCornerShape(topStart = smallRadius, topEnd = smallRadius, bottomStart = cornerRadius, bottomEnd = cornerRadius)
                    else -> RoundedCornerShape(smallRadius)
                }

                Surface(
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item()
                }
            }
        }
    }
}

@Composable
fun SettingsItemCell(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    isEnabled: Boolean = true
) {
    SettingsCustomItem(onClick = { if (isEnabled) onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .alpha(if (isEnabled) 1.0f else 0.38f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isError: Boolean = false,
    isEnabled: Boolean = true
) {
    SettingsCustomItem(onClick = { if (isEnabled) onCheckedChange(!checked) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = when {
                    isError -> MaterialTheme.colorScheme.error
                    !isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else -> MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (isEnabled) 1f else 0.38f
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = isEnabled,
                thumbContent = {
                    Icon(
                        imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsCustomItem(
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (onClick != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            content()
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}
