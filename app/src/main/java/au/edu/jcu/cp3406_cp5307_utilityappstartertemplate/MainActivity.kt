package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp()
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
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }

    var studyMinutes by remember { mutableIntStateOf(25) }
    var breakMinutes by remember { mutableIntStateOf(5) }
    var showProgressBar by remember { mutableStateOf(true) }
    var showQuote by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(false) }

    var isStudyMode by remember { mutableStateOf(true) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(studyMinutes * 60) }

    val totalSeconds = if (isStudyMode) studyMinutes * 60 else breakMinutes * 60
    val progress = if (totalSeconds > 0) {
        1f - remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else {
        0f
    }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }

        if (isRunning && remainingSeconds == 0) {
            isStudyMode = !isStudyMode
            remainingSeconds = if (!isStudyMode) {
                breakMinutes * 60
            } else {
                studyMinutes * 60
            }
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
                    isStudyMode = isStudyMode,
                    remainingSeconds = remainingSeconds,
                    progress = progress,
                    showProgressBar = showProgressBar,
                    showQuote = showQuote,
                    isRunning = isRunning,
                    onStartPauseClick = { isRunning = !isRunning },
                    onResetClick = {
                        isRunning = false
                        remainingSeconds = if (isStudyMode) {
                            studyMinutes * 60
                        } else {
                            breakMinutes * 60
                        }
                    }
                )

                "Settings" -> SettingsScreen(
                    studyMinutes = studyMinutes,
                    breakMinutes = breakMinutes,
                    showProgressBar = showProgressBar,
                    showQuote = showQuote,
                    vibrationEnabled = vibrationEnabled,
                    onStudyMinutesChange = {
                        studyMinutes = it
                        if (isStudyMode && !isRunning) {
                            remainingSeconds = studyMinutes * 60
                        }
                    },
                    onBreakMinutesChange = {
                        breakMinutes = it
                        if (!isStudyMode && !isRunning) {
                            remainingSeconds = breakMinutes * 60
                        }
                    },
                    onShowProgressBarChange = { showProgressBar = it },
                    onShowQuoteChange = { showQuote = it },
                    onVibrationEnabledChange = { vibrationEnabled = it }
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
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit
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
                        text = "Stay focused. Small steps still move you forward.",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
    onVibrationEnabledChange: (Boolean) -> Unit
) {
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

        SettingChipGroup(
            title = "Study duration",
            selectedValue = studyMinutes,
            options = listOf(25, 30, 45),
            suffix = "min",
            onValueChange = onStudyMinutesChange
        )

        SettingChipGroup(
            title = "Break duration",
            selectedValue = breakMinutes,
            options = listOf(5, 10, 15),
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