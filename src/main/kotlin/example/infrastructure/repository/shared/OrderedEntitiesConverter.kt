package example.infrastructure.repository.shared

object OrderedEntitiesConverter {
    fun <E> convert(source: List<E>): Collection<OrderedEntity<E>> {
        return source.mapIndexed { index, source -> OrderedEntity(index, source) }
    }

    fun <E> restore(orderingEntities: Collection<OrderedEntity<E>>): List<E> {
        return orderingEntities.sortedBy { it.ordering }.map { it.value }.toList()
    }
}