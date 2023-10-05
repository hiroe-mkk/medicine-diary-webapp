package example.infrastructure.repository.shared

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*

internal class OrderedEntitiesConverterTest {
    @Test
    fun canConvert() {
        //given:
        val source = listOf("a", "b", "c", "d", "e")

        //when:
        val actual = OrderedEntitiesConverter.convert(source)

        //then:
        val expected = arrayOf(OrderedEntity(0, "a"),
                               OrderedEntity(1, "b"),
                               OrderedEntity(2, "c"),
                               OrderedEntity(3, "d"),
                               OrderedEntity(4, "e"))
        assertThat(actual).containsExactlyInAnyOrder(*expected)
    }

    @Test
    fun canRestore() {
        //given:
        val orderedEntities = setOf(OrderedEntity(0, "a"),
                                    OrderedEntity(1, "b"),
                                    OrderedEntity(2, "c"),
                                    OrderedEntity(3, "d"),
                                    OrderedEntity(4, "e"))

        //when:
        val actual = OrderedEntitiesConverter.restore(orderedEntities)

        //then:
        val expected = arrayOf("a", "b", "c", "d", "e")
        assertThat(actual).containsExactly(*expected)
    }
}