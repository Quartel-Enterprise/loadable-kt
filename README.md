# Loadable

A tiny Kotlin Multiplatform type for a value that is either still loading or already loaded.

```kotlin
sealed interface Loadable<out T> {
    data object Loading : Loadable<Nothing>
    data class Loaded<out T>(val value: T) : Loadable<T>
}
```

That is the whole type (shown without `public` and its one factory function), plus a few extensions to create, read
and transform it. It depends only on the Kotlin standard library: no coroutines, no Compose, no Android. Use it
anywhere a value may not be there yet: UI state, a ViewModel or presenter, a repository, a cache, a server.

It shines when parts of a screen load at different times. Make each part of the state a `Loadable`, and every
section can show its own placeholder (a shimmer, for example) instead of one full-screen spinner.

```kotlin
data class ProfileUiState(
    val name: String,
    val photo: Loadable<String?>,
    val plans: Loadable<List<Plan>>,
)
```

## Why not...

- **A nullable (`T?`)?** `null` can't say whether the value is still loading or loaded as `null`.
  `Loadable<String?>` can: `Loading` is "not yet", `Loaded(null)` is "this user has no photo".
- **A sealed `UiState` (`Loading` / `Success` / `Error`)?** One state for the whole screen means one spinner for the
  whole screen. A per-section sealed class works, but you write a new one for every section. `Loadable<T>` is generic
  and reusable.
- **`kotlin.Result`?** It holds the outcome of something that has finished, success or failure. It has no "not yet"
  state.

## What it doesn't do

`Loadable` has two states and nothing else: no error, empty, progress or refreshing state. Keep failures where
they belong, for example a separate field in your state, or a `Loadable<Result<T>>`. If you need more states, a
sealed type of your own is a better fit.

## Installation

Available on Maven Central.

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

```kotlin
dependencies {
    implementation(libs.quare.loadable)
}
```

In a multiplatform project, add it to `commonMain`.

### Platforms

| Platform | Targets |
| --- | --- |
| JVM and Android | `jvm` (Java 11 bytecode; Android uses the JVM artifact) |
| JavaScript | `js`, `wasmJs` |
| Apple | `iosArm64`, `iosSimulatorArm64`, `macosArm64` |
| Linux | `linuxX64`, `linuxArm64` |
| Windows | `mingwX64` |

There are no Intel Apple targets (`iosX64`, `macosX64`), tvOS or watchOS targets. Built with Kotlin 2.4.21.

## Usage

Everything is in the `software.quare.loadable` package.

```kotlin
import software.quare.loadable.Loadable

val loading: Loadable<User> = Loadable.Loading
val loaded: Loadable<User> = Loadable.Loaded(user)
```

`Loadable` is covariant, so `Loadable.Loading` fits any `Loadable<T>`.

Match on it with `when`. It's a sealed interface, so the compiler checks that both states are handled:

```kotlin
when (val photo = state.photo) {
    Loadable.Loading -> PhotoShimmer()
    is Loadable.Loaded -> Photo(url = photo.value)
}
```

### Creating

```kotlin
user.toLoaded()                        // Loadable.Loaded(user)
listOf(a, b).toLoaded()                // Loadable.Loaded(listOf(a, b)): the whole list is one value
listOf(a, b).toLoadedItems()           // listOf(Loadable.Loaded(a), Loadable.Loaded(b))
Loadable.createLoadingItems(count = 3) // three Loadable.Loading placeholders, empty for a negative count
```

`createLoadingItems` is handy for a list that shows placeholder rows while it loads.

### Reading

```kotlin
loadable.valueOrNull()   // the value, or null while loading
loadable.loadedOrNull()  // the Loadable.Loaded, or null while loading
loadable.isLoading()
loadable.isLoaded()
items.toLoadedValues()   // the non-null values of the loaded items of a List<Loadable<T>>
```

`valueOrNull()` can't tell a loaded `null` from loading. When the value is nullable, use `loadedOrNull()` or `when`.
For the same reason, `toLoadedValues()` drops loaded `null` items as well as the loading ones.

`isLoaded()` and `isLoading()` smart-cast, so `if (loadable.isLoaded()) loadable.value` compiles, and so does
`if (!loadable.isLoading()) loadable.value`. When both states lead somewhere, prefer `when`: the compiler checks that
each one is handled.

### Transforming

```kotlin
val name: Loadable<String> = userLoadable.map(User::name) // stays Loading while the user loads
```

## Example: sections that load on their own

Start every section as `Loading`, then fill each one in when its data arrives. This uses
[kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines); `Loadable` itself doesn't need it.

```kotlin
class ProfilePresenter(
    private val users: UserRepository,
    private val plans: PlanRepository,
    scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(
        ProfileUiState(
            name = "",
            photo = Loadable.Loading,
            plans = Loadable.Loading,
        ),
    )
    val state: StateFlow<ProfileUiState> = _state

    init {
        scope.launch {
            val photoUrl = users.getPhotoUrl()
            _state.update { it.copy(photo = photoUrl.toLoaded()) }
        }
        scope.launch {
            val userPlans = plans.getPlans()
            _state.update { it.copy(plans = userPlans.toLoaded()) }
        }
    }
}
```

With flows, `stateIn` gives each section `Loading` until its first value:

```kotlin
val plans: StateFlow<Loadable<List<Plan>>> = planRepository
    .observePlans()
    .map { it.toLoaded() }
    .stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loadable.Loading,
    )
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

One behavior change: `mapToStatus` turned a loaded `null` into `Loading`. `map` keeps it `Loaded`.

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
