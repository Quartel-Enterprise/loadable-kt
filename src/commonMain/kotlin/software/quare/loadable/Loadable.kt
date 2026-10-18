package software.quare.loadable

public sealed interface Loadable<out T> {
    public data object Loading : Loadable<Nothing>

    public data class Loaded<out T>(
        val value: T,
    ) : Loadable<T>

    public companion object {
        public fun createLoadingItems(count: Int): List<Loading> = List(count.coerceAtLeast(0)) { Loading }
    }
}
