package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.FocusViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.ThemeViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme.ThemeMode
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.media.RingtoneManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()

            CP3406_CP5603UtilityAppStarterTemplateTheme(themeMode = themeMode) {
                UtilityApp(themeViewModel = themeViewModel,)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp(
    focusViewModel: FocusViewModel = viewModel(),
    themeViewModel: ThemeViewModel? = null,
    startRepeatingVibration: (Context) -> Unit = ::startRepeatingVibration
) {
    var selectedTab by remember { mutableStateOf("Utility") }

    val uiState by focusViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var previousStudyMode by remember { mutableStateOf(uiState.isStudyMode) }
    val totalSeconds = if (uiState.isStudyMode) {
        uiState.studyMinutes * 60
    } else {
        uiState.breakMinutes * 60
    }

    val progress = if (totalSeconds > 0) {
        1f - uiState.remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else {
        0f
    }
    LaunchedEffect(uiState.isStudyMode) {
        if (previousStudyMode != uiState.isStudyMode) {
            val message = if (uiState.isStudyMode) {
                "Study mode started"
            } else {
                "Break mode started"
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            previousStudyMode = uiState.isStudyMode
        }
    }
    LaunchedEffect(uiState.isReminderActive) {
        if (uiState.isReminderActive) {
            if (uiState.vibrationEnabled) {
                startRepeatingVibration(context)
            }

            while (uiState.isReminderActive) {
                playReminderSound(context)
                delay(2500)
            }
        } else {
            stopVibration(context)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(
                    quoteText = uiState.quoteText,
                    isQuoteLoading = uiState.isQuoteLoading,
                    onRefreshQuoteClick = focusViewModel::loadFocusQuote,
                    isStudyMode = uiState.isStudyMode,
                    remainingSeconds = uiState.remainingSeconds,
                    progress = progress,
                    showProgressBar = uiState.showProgressBar,
                    showQuote = uiState.showQuote,
                    isRunning = uiState.isRunning,
                    onStartPauseClick = focusViewModel::startPauseTimer,
                    onResetClick = focusViewModel::resetTimer,
                    isReminderActive = uiState.isReminderActive,
                    onStopReminderClick = focusViewModel::stopReminder
                )

                "Settings" -> SettingsScreen(
                    studyMinutes = uiState.studyMinutes,
                    breakMinutes = uiState.breakMinutes,
                    showProgressBar = uiState.showProgressBar,
                    showQuote = uiState.showQuote,
                    vibrationEnabled = uiState.vibrationEnabled,
                    onStudyMinutesChange = focusViewModel::updateStudyMinutes,
                    onBreakMinutesChange = focusViewModel::updateBreakMinutes,
                    onShowProgressBarChange = focusViewModel::updateShowProgressBar,
                    onShowQuoteChange = focusViewModel::updateShowQuote,
                    onVibrationEnabledChange = focusViewModel::updateVibrationEnabled,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}


@Composable
fun UtilityScreen(
    isStudyMode: Boolean,
    remainingSeconds: Int,
    progress: Float,
    showProgressBar: Boolean,
    showQuote: Boolean,
    isRunning: Boolean,
    quoteText: String,
    isQuoteLoading: Boolean,
    onRefreshQuoteClick: () -> Unit,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    isReminderActive: Boolean,
    onStopReminderClick: () -> Unit
) {
    val modeText = if (isStudyMode) "Study Mode" else "Break Mode"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FocusMate",
            style = MaterialTheme.typography.headlineLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = modeText,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = formatTime(remainingSeconds),
                    style = MaterialTheme.typography.displayLarge
                )

                if (showProgressBar) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showQuote) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Daily Focus Quote",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = if (isQuoteLoading) "Loading..." else quoteText,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    OutlinedButton(onClick = onRefreshQuoteClick) {
                        Text("Refresh Quote")
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onStartPauseClick) {
                Text(if (isRunning) "Pause" else "Start")
            }

            OutlinedButton(onClick = onResetClick) {
                Text("Reset")
            }
        }
        if (isReminderActive) {
            Button(onClick = onStopReminderClick) {
                Text("Stop Reminder")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    studyMinutes: Int,
    breakMinutes: Int,
    showProgressBar: Boolean,
    showQuote: Boolean,
    vibrationEnabled: Boolean,
    onStudyMinutesChange: (Int) -> Unit,
    onBreakMinutesChange: (Int) -> Unit,
    onShowProgressBarChange: (Boolean) -> Unit,
    onShowQuoteChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    themeViewModel: ThemeViewModel? = null
) {
    val themeMode by themeViewModel?.themeMode?.collectAsState() ?: remember { mutableStateOf(ThemeMode.AUTO) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        // Theme selection
        if (themeViewModel != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Theme: ${themeMode.name}",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = themeMode == mode,
                            onClick = { themeViewModel.updateThemeMode(mode) },
                            label = { Text(mode.name) }
                        )
                    }
                }
            }
        }

        SettingChipGroup(
            title = "Study duration",
            selectedValue = studyMinutes,
            options = listOf(1, 25, 30, 45),
            suffix = "min",
            onValueChange = onStudyMinutesChange
        )

        SettingChipGroup(
            title = "Break duration",
            selectedValue = breakMinutes,
            options = listOf(1, 5, 10, 15),
            suffix = "min",
            onValueChange = onBreakMinutesChange
        )

        SettingSwitchRow(
            title = "Show progress bar",
            checked = showProgressBar,
            onCheckedChange = onShowProgressBarChange
        )

        SettingSwitchRow(
            title = "Show daily quote",
            checked = showQuote,
            onCheckedChange = onShowQuoteChange
        )

        SettingSwitchRow(
            title = "Vibration reminder",
            checked = vibrationEnabled,
            onCheckedChange = onVibrationEnabledChange
        )
    }
}

@Composable
fun SettingChipGroup(
    title: String,
    selectedValue: Int,
    options: List<Int>,
    suffix: String,
    onValueChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$title: $selectedValue $suffix",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedValue == option,
                    onClick = { onValueChange(option) },
                    label = { Text("$option $suffix") }
                )
            }
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

fun startRepeatingVibration(context: Context) {
    val pattern = longArrayOf(
        0,    // 立刻开始
        600,  // 震动 600ms
        400,  // 停 400ms
        600,  // 震动 600ms
        1000  // 停 1000ms
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

        val vibrator = vibratorManager.defaultVibrator

        vibrator.vibrate(
            VibrationEffect.createWaveform(pattern, 0)
        )
    } else {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, 0)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }
    }
}
fun playReminderSound(context: Context) {
    try {
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, notificationUri)
        ringtone.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun stopVibration(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

        vibratorManager.defaultVibrator.cancel()
    } else {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}