# Loadable

A tiny Kotlin Multiplatform type for a value that is either still loading or already loaded.

It shines when only parts of a screen load at different times: each part of the UI state is a `Loadable`, so every
section can show its own placeholder (a shimmer, for example) instead of one full-screen spinner.

```kotlin
data class ProfileUiState(
    val name: String,
    val photo: Loadable<String?>,
    val plans: Loadable<List<Plan>>,
)
```

Platforms: JVM (and Android), JS, Wasm/JS, iOS, macOS, Linux and Windows.

## Installation

```kotlin
dependencies {
    implementation("software.quare:loadable:1.0.0")
}
```

Or with a version catalog:

```toml
[versions]
quare-loadable = "1.0.0"

[libraries]
quare-loadable = { module = "software.quare:loadable", version.ref = "quare-loadable" }
```

In a multiplatform project, add it to `commonMain`.

## Usage

```kotlin
val loading: Loadable<User> = Loadable.Loading
val loaded: Loadable<User> = Loadable.Loaded(user)

when (state.photo) {
    Loadable.Loading -> PhotoShimmer()
    is Loadable.Loaded -> Photo(url = state.photo.value)
}
```

### Creating

```kotlin
user.toLoaded()                       // Loadable.Loaded(user)
listOf(a, b).toLoaded()               // Loadable.Loaded(listOf(a, b)): the whole list is one value
listOf(a, b).toLoadedItems()          // listOf(Loadable.Loaded(a), Loadable.Loaded(b))
Loadable.createLoadingItems(count = 3) // three Loadable.Loading placeholders, empty for a negative count
```

### Reading

```kotlin
loadable.valueOrNull()   // the value, or null while loading
loadable.loadedOrNull()  // the Loadable.Loaded, or null while loading
loadable.isLoading()
loadable.isLoaded()
items.toLoadedValues()   // the values of the loaded items of a List<Loadable<T>>, skipping the loading ones
```

`valueOrNull()` can't tell a loaded `null` from loading: check `isLoaded()` or match on the type when the value is
nullable.

### Transforming

```kotlin
val name: Loadable<String> = userLoadable.map(User::name) // stays Loading while the user loads
```

## Migrating from `software.quare:data-status`

`Loadable` replaces [data-status](https://github.com/Quartel-Enterprise/data-status), which is archived. It's also
multiplatform, where data-status was JVM-only.

| data-status | loadable |
| --- | --- |
| `software.quare.data_status.DataStatus` | `software.quare.loadable.Loadable` |
| `DataStatus.Loaded(data)` / `.data` | `Loadable.Loaded(value)` / `.value` |
| `value.toLoadedStatus()` | `value.toLoaded()` |
| `list.toLoadedStatus()` (each item) | `list.toLoadedItems()` |
| `status.toLoadedInformation()` | `loadable.loadedOrNull()` |
| `status.toLoadedData()` | `loadable.valueOrNull()` |
| `statuses.toLoadedData()` (list) | `loadables.toLoadedValues()` |
| `status.mapToStatus { name }` | `loadable.map { it.name }` |
| `status.isLoading()` | `loadable.isLoading()` |
| `DataStatusUtils.generateLoading(n)` | `Loadable.createLoadingItems(n)` |

## Development

```bash
./gradlew allTests     # every target (Apple targets need macOS)
./scripts/ktlint.sh    # lint with the Quare ruleset (--format to fix)
```

## Releasing

1. Bump `version` in `build.gradle.kts` and merge it to `main`.
2. Publish a GitHub release whose tag is that version (`1.2.3`). The `Publish` workflow checks the tag, runs the
   tests and releases the signed artifacts to Maven Central, where they show up a few minutes later.

## License

[Apache License 2.0](LICENSE)
