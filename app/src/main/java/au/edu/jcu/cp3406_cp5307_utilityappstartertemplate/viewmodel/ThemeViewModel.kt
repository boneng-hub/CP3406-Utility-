package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.ThemePreferencesRepository
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ThemePreferencesRepository(application)

    val themeMode: StateFlow<ThemeMode> = repository.themeModeFlow
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, ThemeMode.AUTO)

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }
}

