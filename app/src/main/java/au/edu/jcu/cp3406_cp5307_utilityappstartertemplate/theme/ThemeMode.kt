package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.theme

enum class ThemeMode {
    AUTO,
    LIGHT,
    DARK
}

fun ThemeMode.toStorageValue(): String {
    return when (this) {
        ThemeMode.AUTO -> "auto"
        ThemeMode.LIGHT -> "light"
        ThemeMode.DARK -> "dark"
    }
}

fun themeModeFromStorage(value: String?): ThemeMode {
    return when (value) {
        "light" -> ThemeMode.LIGHT
        "dark" -> ThemeMode.DARK
        else -> ThemeMode.AUTO
    }
}

