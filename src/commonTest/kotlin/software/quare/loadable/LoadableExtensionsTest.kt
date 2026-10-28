package software.quare.loadable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class LoadableExtensionsTest {
    @Test
    fun `GIVEN a value WHEN wrapping it THEN returns it as Loaded`() {
        // Given
        val name = "John"

        // When
        val result = name.toLoaded()

        // Then
        assertEquals(Loadable.Loaded("John"), result)
    }

    @Test
    fun `GIVEN a list WHEN wrapping it THEN returns the whole list as a single Loaded value`() {
        // Given
        val names = listOf("John", "Jane")

        // When
        val result: Loadable<List<String>> = names.toLoaded()

        // Then
        assertEquals(Loadable.Loaded(listOf("John", "Jane")), result)
    }

    @Test
    fun `GIVEN a list WHEN wrapping each item THEN returns one Loaded value per item`() {
        // Given
        val names = listOf("John", "Jane")

        // When
        val result = names.toLoadedItems()

        // Then
        assertEquals(listOf(Loadable.Loaded("John"), Loadable.Loaded("Jane")), result)
    }

    @Test
    fun `GIVEN a Loaded value WHEN reading it as Loaded THEN returns it`() {
        // Given
        val loaded: Loadable<User> = Loadable.Loaded(
            User(
                name = "John",
                photo = null,
            ),
        )

        // When
        val result = loaded.loadedOrNull()

        // Then
        assertEquals<Loadable<User>?>(loaded, result)
    }

    @Test
    fun `GIVEN Loading WHEN reading it as Loaded THEN returns null`() {
        // Given
        val loading: Loadable<User> = Loadable.Loading

        // When
        val result = loading.loadedOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `GIVEN a Loaded value WHEN reading its value THEN returns the value`() {
        // Given
        val loaded: Loadable<User> = Loadable.Loaded(
            User(
                name = "John",
                photo = null,
            ),
        )

        // When
        val result = loaded.valueOrNull()

        // Then
        assertEquals(
            User(
                name = "John",
                photo = null,
            ),
            result,
        )
    }

    @Test
    fun `GIVEN Loading WHEN reading its value THEN returns null`() {
        // Given
        val loading: Loadable<User> = Loadable.Loading

        // When
        val result = loading.valueOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `GIVEN Loaded and Loading items WHEN reading the loaded values THEN skips the Loading ones`() {
        // Given
        val items = listOf(Loadable.Loaded("John"), Loadable.Loading, Loadable.Loaded("Jane"))

        // When
        val result = items.toLoadedValues()

        // Then
        assertEquals(listOf("John", "Jane"), result)
    }

    @Test
    fun `GIVEN Loading WHEN checking the state THEN is loading and not loaded`() {
        // Given
        val loading: Loadable<User> = Loadable.Loading

        // When
        val isLoading = loading.isLoading()
        val isLoaded = loading.isLoaded()

        // Then
        assertTrue(isLoading)
        assertFalse(isLoaded)
    }

    @Test
    fun `GIVEN a Loaded value WHEN checking the state THEN is loaded and not loading`() {
        // Given
        val loaded: Loadable<User> = Loadable.Loaded(
            User(
                name = "John",
                photo = null,
            ),
        )

        // When
        val isLoading = loaded.isLoading()
        val isLoaded = loaded.isLoaded()

        // Then
        assertFalse(isLoading)
        assertTrue(isLoaded)
    }

    @Test
    fun `GIVEN a Loaded value WHEN mapping it THEN returns the transformed value as Loaded`() {
        // Given
        val loaded: Loadable<User> = Loadable.Loaded(
            User(
                name = "John",
                photo = null,
            ),
        )

        // When
        val result = loaded.map(User::name)

        // Then
        assertEquals(Loadable.Loaded("John"), result)
    }

    @Test
    fun `GIVEN a Loaded value WHEN mapping it to null THEN returns Loaded null`() {
        // Given
        val loaded: Loadable<User> = Loadable.Loaded(
            User(
                name = "John",
                photo = null,
            ),
        )

        // When
        val result = loaded.map(User::photo)

        // Then
        assertEquals(Loadable.Loaded(null), result)
    }

    @Test
    fun `GIVEN Loading WHEN mapping it THEN stays Loading without calling the transform`() {
        // Given
        val loading: Loadable<User> = Loadable.Loading
        var calls = 0

        // When
        val result = loading.map {
            calls++
            it.name
        }

        // Then
        assertEquals(Loadable.Loading, result)
        assertEquals(0, calls)
    }
}
