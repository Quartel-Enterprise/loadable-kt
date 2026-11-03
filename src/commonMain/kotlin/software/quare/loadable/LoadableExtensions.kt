package software.quare.loadable

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmName

public fun <T> T.toLoaded(): Loadable.Loaded<T> = Loadable.Loaded(this)

// Why: not an overload of toLoaded, which would make a List impossible to wrap as a single Loaded value
public fun <T> List<T>.toLoadedItems(): List<Loadable.Loaded<T>> = map(Loadable<T>::Loaded)

public fun <T> Loadable<T>.loadedOrNull(): Loadable.Loaded<T>? = this as? Loadable.Loaded<T>

public fun <T> Loadable<T>.valueOrNull(): T? = loadedOrNull()?.value

/**
 * Returns the values of the [Loadable.Loaded] items, in order, skipping the [Loadable.Loading] ones.
 *
 * Loaded `null` values are skipped too, so the result can be shorter than the number of loaded items. To keep them,
 * use `mapNotNull { it.loadedOrNull() }.map { it.value }`.
 */
public fun <T> List<Loadable<T>>.toLoadedValues(): List<T> = mapNotNull { it.valueOrNull() }

/**
 * Returns `true` while this is [Loadable.Loading].
 *
 * The compiler smart-casts this to [Loadable.Loading] when it returns `true`, and to [Loadable.Loaded] when it
 * returns `false`.
 */
@OptIn(ExperimentalContracts::class)
public fun <T> Loadable<T>.isLoading(): Boolean {
    contract {
        returns(true) implies (this@isLoading is Loadable.Loading)
        returns(false) implies (this@isLoading is Loadable.Loaded<T>)
    }
    return this is Loadable.Loading
}

/**
 * Returns `true` once this is [Loadable.Loaded].
 *
 * The compiler smart-casts this to [Loadable.Loaded] when it returns `true`, so `if (loadable.isLoaded())
 * loadable.value` compiles, and to [Loadable.Loading] when it returns `false`.
 */
@OptIn(ExperimentalContracts::class)
public fun <T> Loadable<T>.isLoaded(): Boolean {
    contract {
        returns(true) implies (this@isLoaded is Loadable.Loaded<T>)
        returns(false) implies (this@isLoaded is Loadable.Loading)
    }
    return this is Loadable.Loaded
}

/*
 * Why: keeps the 1.0.0 klib signature (Loadable<*> receiver) for binaries built against it; the JVM signature is
 * already kept by the generic overload, so this one only needs another JVM name
 */
@Deprecated(
    message = "Kept for binary compatibility with 1.0.0.",
    level = DeprecationLevel.HIDDEN,
)
@JvmName("isLoadingStarProjected")
public fun Loadable<*>.isLoading(): Boolean = this is Loadable.Loading

@Deprecated(
    message = "Kept for binary compatibility with 1.0.0.",
    level = DeprecationLevel.HIDDEN,
)
@JvmName("isLoadedStarProjected")
public fun Loadable<*>.isLoaded(): Boolean = this is Loadable.Loaded

public inline fun <T, R> Loadable<T>.map(transform: (T) -> R): Loadable<R> = when (this) {
    Loadable.Loading -> Loadable.Loading
    is Loadable.Loaded -> Loadable.Loaded(transform(value))
}
