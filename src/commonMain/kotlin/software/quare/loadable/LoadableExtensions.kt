package software.quare.loadable

public fun <T> T.toLoaded(): Loadable.Loaded<T> = Loadable.Loaded(this)

// Why: not an overload of toLoaded, which would make a List impossible to wrap as a single Loaded value
public fun <T> List<T>.toLoadedItems(): List<Loadable.Loaded<T>> = map(Loadable<T>::Loaded)

public fun <T> Loadable<T>.loadedOrNull(): Loadable.Loaded<T>? = this as? Loadable.Loaded<T>

public fun <T> Loadable<T>.valueOrNull(): T? = loadedOrNull()?.value

public fun <T> List<Loadable<T>>.toLoadedValues(): List<T> = mapNotNull { it.valueOrNull() }

public fun Loadable<*>.isLoading(): Boolean = this is Loadable.Loading

public fun Loadable<*>.isLoaded(): Boolean = this is Loadable.Loaded

public inline fun <T, R> Loadable<T>.map(transform: (T) -> R): Loadable<R> = when (this) {
    Loadable.Loading -> Loadable.Loading
    is Loadable.Loaded -> Loadable.Loaded(transform(value))
}
