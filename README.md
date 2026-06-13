# FocusMate – CP3406 / CP5307 Utility App

FocusMate is a simple utility-style Android app built for **CP3406 Assessment 1: Utility App**. It is designed to give users an at-a-glance focus timer with a small number of clear actions and a dedicated settings screen.

## What the app does

FocusMate is a focused study/break timer that helps users:

- start, pause, and reset a timer
- switch between **study mode** and **break mode** automatically
- view an optional progress bar
- fetch and display a daily focus quote from an external API
- change study/break durations and UI preferences in a settings screen
- choose a light, dark, or automatic theme

The app uses a bottom navigation bar to switch between the two required screens:

- **Utility** screen: main timer and quote view
- **Settings** screen: timer and appearance preferences

## Core features

### Utility screen
- Shows the current mode (`Study Mode` / `Break Mode`)
- Displays the remaining time in `MM:SS` format
- Includes start/pause and reset actions
- Optionally shows a progress bar
- Optionally shows a daily quote fetched from ZenQuotes

### Settings screen
- Adjusts study duration
- Adjusts break duration
- Toggles the progress bar
- Toggles the quote card
- Toggles vibration reminders
- Selects app theme: `AUTO`, `LIGHT`, or `DARK`

### Architecture and data flow
- **`MainActivity.kt`** hosts the Compose UI and bottom navigation layout
- **`FocusViewModel`** stores timer state and UI options
- **`QuoteRepository`** fetches a quote from the web using Retrofit
- **`ThemePreferencesRepository`** stores theme settings in DataStore
- **`AppContainer`** provides a lightweight manual dependency container

## Technology used

- Kotlin
- Jetpack Compose
- Material Design 3
- ViewModel / StateFlow
- Repository pattern
- Retrofit + Gson
- DataStore Preferences
- Android vibration APIs

## Running the project

1. Open the project in **Android Studio**
2. Let Gradle sync finish
3. Run the `app` module on an emulator or device

### Requirements
- Android Studio with a recent Android SDK installed
- Internet permission enabled for quote fetching
- A device/emulator capable of running the app's minimum SDK

## Project structure

```text
app/src/main/java/au/edu/jcu/cp3406_cp5307_utilityappstartertemplate/
├── MainActivity.kt
├── data/
│   ├── QuoteApi.kt
│   ├── QuoteRepository.kt
│   ├── QuoteResponse.kt
│   └── ThemePreferencesRepository.kt
├── di/
│   └── AppContainer.kt
├── theme/
│   └── ThemeMode.kt
└── viewmodel/
	├── FocusUiState.kt
	├── FocusViewModel.kt
	└── ThemeViewModel.kt
```

## Assignment requirement check

Based on the current source code in this repository, the project appears to satisfy the main technical requirements of the brief:

| Requirement from brief | Status in this project | Evidence |
|---|---|---|
| Kotlin and Android Studio setup | Implemented | Kotlin Android project with Gradle and Compose |
| Jetpack Compose layouts | Implemented | Main UI is built with Compose in `MainActivity.kt` |
| Material Design 3 | Implemented | Uses Material 3 components and theme support |
| Main screen + settings screen | Implemented | Bottom navigation switches between Utility and Settings |
| ViewModel / app architecture | Implemented | `FocusViewModel` and `ThemeViewModel` manage state |
| Repository pattern | Implemented | `QuoteRepository` and `ThemePreferencesRepository` |
| Networking with Retrofit | Implemented | Quotes are fetched from `https://zenquotes.io/api/random` |
| GitHub version control | Partially verifiable here | A Git history exists with several meaningful commits; sharing/commit frequency should be checked manually |
| README documentation | Implemented | This README documents the app and implementation |
| Self-reflection submission | Not included in source code | Must be submitted separately as the brief requires |

### Notes on compliance
- The project is **very close to the assignment brief** and already demonstrates the key Week 1–5 topics.
- One implementation detail: dependency injection is handled with a **lightweight manual container** (`AppContainer`) rather than a full framework like Hilt. That still supports clean separation, but if your marker expects a formal DI framework you may want to mention that in your reflection.
- The settings screen in this app **does persist theme selection** via DataStore, which is fine even though the assignment says settings do not need to be persistent.

## Git history

The repository already contains a small but meaningful commit history, including commits for:

- building the basic timer UI
- moving timer state into a ViewModel
- adding the Retrofit quote feature
- adding vibration reminders

That is a good sign for the GitHub usage criterion, although you should still make sure the repository is shared correctly for submission.

## Self-reflection reminder

The assignment also requires a separate **500-word self-reflection PDF** based on Gibbs’ Reflective Cycle. This repository does not include that document, so make sure it is prepared and submitted separately.

## License

This project is provided for educational use as part of CP3406 / CP5307 assessment work.
