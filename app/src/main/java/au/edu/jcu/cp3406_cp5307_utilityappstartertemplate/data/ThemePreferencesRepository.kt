package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme.ThemeMode
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme.themeModeFromStorage
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme.toStorageValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferencesRepository(private val context: Context) {
    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val value = preferences[THEME_MODE_KEY]
        themeModeFromStorage(value)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.toStorageValue()
        }
    }
}
