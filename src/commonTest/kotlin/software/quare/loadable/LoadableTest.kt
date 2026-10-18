package software.quare.loadable

import kotlin.test.Test
import kotlin.test.assertEquals

internal class LoadableTest {
    @Test
    fun `GIVEN a positive count WHEN creating loading items THEN returns that many Loading items`() {
        // Given
        val count = 3

        // When
        val items = Loadable.createLoadingItems(count)

        // Then
        assertEquals(listOf(Loadable.Loading, Loadable.Loading, Loadable.Loading), items)
    }

    @Test
    fun `GIVEN a zero count WHEN creating loading items THEN returns an empty list`() {
        // Given
        val count = 0

        // When
        val items = Loadable.createLoadingItems(count)

        // Then
        assertEquals(emptyList(), items)
    }

    @Test
    fun `GIVEN a negative count WHEN creating loading items THEN returns an empty list`() {
        // Given
        val count = -2

        // When
        val items = Loadable.createLoadingItems(count)

        // Then
        assertEquals(emptyList(), items)
    }
}
