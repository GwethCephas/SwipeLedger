# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

SwipeLedger is a native Android app (Kotlin + Jetpack Compose) that automatically builds a bookkeeping ledger for small/informal businesses by parsing incoming M-Pesa-style SMS confirmations (Kenyan mobile money) in the background, classifying them into expense categories, and storing them in an encrypted local database. There is no backend — everything is on-device.

## Build & test commands

Standard Gradle wrapper commands from the project root (PowerShell):

```
./gradlew.bat assembleDebug              # build debug APK
./gradlew.bat build                      # full build (all modules)
./gradlew.bat test                       # run all JVM unit tests (all modules)
./gradlew.bat :feature:test              # unit tests for one module (app | core | feature)
./gradlew.bat :feature:test --tests "com.cephcoding.feature.parser.RegexParserTest"   # single test class
./gradlew.bat :feature:test --tests "com.cephcoding.feature.parser.RegexParserTest.methodName"  # single test method
./gradlew.bat connectedAndroidTest        # instrumented tests, needs an emulator/device (e.g. core's TransactionRepositoryTest against real Room/SQLCipher)
./gradlew.bat lint                       # Android lint
```

Unit tests (JUnit4 + MockK + kotlinx-coroutines-test) live under `src/test` in each module and run on the JVM. Instrumented tests (androidx.test/Espresso) live under `src/androidTest` and require a device/emulator, used mainly where Room/SQLCipher need a real SQLite driver.

## Module architecture

Three modules, strict one-directional dependency: `app` → `feature` → `core` (feature also depends directly on core; app depends on both).

- **`core`** — the domain/data layer, no feature-specific UI. Contains:
  - `domain.model` — `RawTransaction`, `TransactionType`, `TransactionCategory`/`TransactionSubcategory` (hierarchical taxonomy; `RawTransaction` stores only `subcategory`, `category` is a derived extension property `subcategory?.parent`), `NavRoutes` (shared nav route definitions with title/icon, consumed by `app`'s NavGraph and feature's BottomNavBar).
  - `domain.repository.TransactionRepository` — interface; `data.repository.TransactionRepositoryImpl` is the impl.
  - `data.database` — Room (`TransactionDatabase`, `TransactionEntity`, `TransactionsDao`), opened via `SupportOpenHelperFactory` from SQLCipher (`net.zetetic:sqlcipher-android`) for at-rest encryption. `System.loadLibrary("sqlcipher")` is loaded once in `SwipeLedgerApplication.onCreate()`.
  - `data.mapper.Mapper.kt` — `RawTransaction` ⇄ `TransactionEntity` extension functions.
  - `di.CoreModule` — Koin module providing the encrypted Room DB, DAO, and repository singletons.
  - `ui.theme` — shared Compose theme/colors (`ObsidianBg` etc.) used across `app` and `feature`.
  - `common.util` — cross-cutting helpers, e.g. `AutoStartPermissionManager` (per-OEM autostart-settings intents for Xiaomi/Huawei/Oppo/Vivo, needed because background SMS processing is easily killed by aggressive OEM battery managers).

- **`feature`** — all screens and business logic, organized by feature package (`dashboard`, `transactions`, `events`, `profile`, `sms`, `common`). Each feature with a ViewModel has its own `di/*Module.kt` Koin module (e.g. `dashboard.di.DashboardModule`) registered in `SwipeLedgerApplication`.
  - `sms` is the core pipeline, not a UI screen: `SmsReceiver` (BroadcastReceiver on `SMS_RECEIVED`) enqueues a `SmsParseWorker` (WorkManager `CoroutineWorker`, expedited) per message rather than doing work in the receiver. The worker calls `TransactionProcessor`, which: filters out non-financial senders (`isFinancialSender`), parses the body with `RegexParser` (regex patterns tuned to M-Pesa "sent to X" / "received Ksh Y from Z" confirmation formats) into a `RawTransaction`, classifies it into a `TransactionSubcategory` via `LocalClassifier` (keyword-matching against a hardcoded matrix — e.g. "kplc"/"water" → POWER_AND_WATER, "shell" → FUEL_AND_GAS_STATIONS, "uber"/"bolt" → RIDE_HAILING_AND_TAXIS; `TransactionType.INCOME` always short-circuits to `GENERAL_INCOME`, never falls through to uncategorized), then persists through `TransactionRepository`.
  - `dashboard.presentation.DashboardViewModel` derives all dashboard state (total income/expenses, net cash flow, expense breakdown by category, weekly flow bucketed Mon–Sun) reactively from `repository.getAllTransactions()` via `map`/`stateIn` — there's no separate aggregation/analytics layer, it's computed in the ViewModel each time the transaction list changes.
  - `transactions.presentation` holds list/search/filter UI (`TransactionsScreen`, `CustomSearch`, `TransactionPicker`, `TransactionRow`) — filtering/search is in-memory over the Flow from the repository, not a DB query variant.

- **`app`** — thin shell: `SwipeLedgerApplication` (Koin `startKoin` wiring `coreModule`, `smsModule`, `dashboardModule`, and WorkManager's `workManagerFactory`/custom `Configuration.Provider`), `MainActivity`, and `navigation.NavGraph` (single `NavHost` + bottom nav, screens supplied by `feature`).

## Dependency injection

Koin only (no Hilt/Dagger). Each module's DI file follows the `val xModule = module { ... }` pattern and is registered in `SwipeLedgerApplication.onCreate()`. WorkManager workers are registered with Koin's `worker { ... }` DSL (see `smsModule`) and the app's `workManagerConfiguration` supplies `get()` as the `WorkerFactory` — new `CoroutineWorker`s must be added to a Koin module this way, not constructed directly.

## Notable constraints

- `minSdk` differs per module: `app` = 27, `core`/`feature` = 24. Anything gated on API 27+ features must live in `app` or be guarded with SDK checks in the lower-level modules.
- The SQLCipher passphrase is currently hardcoded in `CoreModule` (`"SuperSecretSecurePassphraseKey123"`) — be aware of this if working on the security/encryption story.
- SMS parsing (`RegexParser`) and category classification (`LocalClassifier`) are pattern/keyword-based, not ML — despite the `ml` package name and the `com.google.mlkit:language-id` dependency, there's no on-device model doing the categorization today.
- `TransactionDatabase` is at version 2 (`core/src/main/java/.../data/database/Migrations.kt` holds `MIGRATION_1_2`, wired via `.addMigrations(...)` in `CoreModule`); `exportSchema = true` with schema JSON under `core/schemas/`. Any future entity change needs a real `Migration`, not `fallbackToDestructiveMigration`.
