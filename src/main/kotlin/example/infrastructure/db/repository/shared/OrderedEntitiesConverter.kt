package example.infrastructure.db.repository.shared

object OrderedEntitiesConverter {
    fun <E> convert(sources: List<E>): Collection<OrderedEntity<E>> {
        return sources.mapIndexed { index, source -> OrderedEntity(index, source) }
    }

    fun <E> restore(orderingEntities: Collection<OrderedEntity<E>>): List<E> {
        return orderingEntities.sortedBy { it.ordering }.map { it.value }.toList()
    }
}
