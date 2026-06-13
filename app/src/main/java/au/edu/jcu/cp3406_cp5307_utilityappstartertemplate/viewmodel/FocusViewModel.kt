package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.QuoteRepository
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.di.AppContainer
class FocusViewModel(
    private val quoteRepository: QuoteRepository = AppContainer.quoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState

    private var timerJob: Job? = null
    init {
        loadFocusQuote()
    }
    fun startPauseTimer() {
        val currentState = _uiState.value

        if (currentState.isRunning) {
            pauseTimer()
        } else {
            _uiState.update {
                it.copy(isRunning = true)
            }
            startTimerJob()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(isRunning = false)
        }
    }

    fun resetTimer() {
        timerJob?.cancel()

        val currentState = _uiState.value
        val resetSeconds = if (currentState.isStudyMode) {
            currentState.studyMinutes * 60
        } else {
            currentState.breakMinutes * 60
        }

        _uiState.update {
            it.copy(
                isRunning = false,
                remainingSeconds = resetSeconds
            )
        }
    }

    private fun startTimerJob() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(1000)

                val currentState = _uiState.value

                if (currentState.remainingSeconds > 1) {
                    _uiState.update {
                        it.copy(remainingSeconds = it.remainingSeconds - 1)
                    }
                } else {
                    switchMode()
                }
            }
        }
    }

    private fun switchMode() {
        val currentState = _uiState.value
        val newStudyMode = !currentState.isStudyMode

        val newRemainingSeconds = if (newStudyMode) {
            currentState.studyMinutes * 60
        } else {
            currentState.breakMinutes * 60
        }

        _uiState.update {
            it.copy(
                isStudyMode = newStudyMode,
                remainingSeconds = newRemainingSeconds
            )
        }
    }

    fun updateStudyMinutes(minutes: Int) {
        val currentState = _uiState.value

        _uiState.update {
            it.copy(
                studyMinutes = minutes,
                remainingSeconds = if (currentState.isStudyMode && !currentState.isRunning) {
                    minutes * 60
                } else {
                    currentState.remainingSeconds
                }
            )
        }
    }

    fun updateBreakMinutes(minutes: Int) {
        val currentState = _uiState.value

        _uiState.update {
            it.copy(
                breakMinutes = minutes,
                remainingSeconds = if (!currentState.isStudyMode && !currentState.isRunning) {
                    minutes * 60
                } else {
                    currentState.remainingSeconds
                }
            )
        }
    }

    fun loadFocusQuote() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isQuoteLoading = true)
            }

            val quote = quoteRepository.getFocusQuote()

            _uiState.update {
                it.copy(
                    quoteText = quote,
                    isQuoteLoading = false
                )
            }
        }
    }
    fun updateShowProgressBar(enabled: Boolean) {
        _uiState.update {
            it.copy(showProgressBar = enabled)
        }
    }

    fun updateShowQuote(enabled: Boolean) {
        _uiState.update {
            it.copy(showQuote = enabled)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(vibrationEnabled = enabled)
        }
    }
}

