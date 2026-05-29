package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

data class FocusUiState(
    val studyMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val remainingSeconds: Int = 25 * 60,
    val isStudyMode: Boolean = true,
    val isRunning: Boolean = false,
    val showProgressBar: Boolean = true,
    val showQuote: Boolean = true,
    val vibrationEnabled: Boolean = false
)