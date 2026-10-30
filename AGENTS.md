# Hisob

Native Android app, Java, Room/SQLite. Main module: `app`. User-facing language: Russian.

## Build
JDK 17, Gradle Wrapper 8.9, Android SDK 35. Run `./gradlew :app:packageRelease` or `./build.sh`.
Debug builds must remain buildable from a clean clone without a private key. The existing local signing key is `.local/signing.jks`; it is ignored by Git. Preserve this key to update the installed Samsung app. Never commit keys, APKs, real expense exports or local device data.

## Verify
`./gradlew :app:assembleDebug :app:assembleDebugAndroidTest`
Install both test APKs on the emulator and run:
`adb -s emulator-5554 shell am instrument -w local.expenses.test/local.expenses.LedgerChecks`
The instrumentation uses isolated in-memory databases and separate test preferences. Never clear or uninstall the user's Samsung app as a test.

## Data invariants
Amounts use integer dirams (`long cents`). Room data access runs on `LedgerDatabase.IO`.
SharedPreferences import is transactional and idempotent. Preserve the original preferences. Never add destructive Room migration fallback. Future schema changes require explicit migrations and migration tests.

## Alif
`PaymentService` reads accessibility content only for `tj.alif.mobi`. Keep the minimum wait and bounded amount retries. Inspect only completed-payment screens during device debugging; financial actions are performed by the user.

## UI and export
Preserve the Russian UI, rounded iOS-style cards, editable detected amounts, user-defined categories, confirmation before expense deletion and UTF-8 CSV escaping.
