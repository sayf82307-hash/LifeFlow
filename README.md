# LifeFlow V1 — Android native (Java)

An offline-first Arabic RTL Android app, with dark navy/blue UI.

## Included
- Dashboard
- Tasks (add/edit/delete/mark complete)
- Calendar appointments (manual date field)
- Habits (add/edit/mark complete)
- Personal goals
- Reminders list (no scheduled notifications yet)
- Notes
- Expenses with total in MAD
- 25-minute focus timer (runs while screen/activity is alive)
- Local persistent data with SharedPreferences JSON

## Limitations
- Reminder notifications are **not implemented**; entries are saved as a list only.
- No full calendar month grid, recurring habits or budget limits yet.
- No cloud sync, accounts, or backups.
- Debug APK only; don't distribute as a production signed release.

## Build on Android phone via GitHub
1. Create a new GitHub repository, upload the entire project contents (including `.github/workflows/android.yml`).
2. Open the repo → Actions → Build LifeFlow APK → Run workflow (or push to main).
3. Once green, open the workflow run → Artifacts → LifeFlow-debug-apk.
4. Download ZIP, extract `app-debug.apk`, install it on Android (allow installation from this source if prompted).

Requires GitHub Actions availability. A GitHub account is free for public repositories under normal usage limits.

Build locally with Android SDK 35, Java 17, Gradle 8.9: `gradle :app:assembleDebug`.
