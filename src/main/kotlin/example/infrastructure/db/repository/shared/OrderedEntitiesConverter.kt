package example.infrastructure.db.repository.shared

object OrderedEntitiesConverter {
    fun <E> convert(source: List<E>): Collection<example.infrastructure.db.repository.shared.OrderedEntity<E>> {
        return source.mapIndexed { index, source ->
            example.infrastructure.db.repository.shared.OrderedEntity(index,
                                                                      source)
        }
    }

    fun <E> restore(orderingEntities: Collection<example.infrastructure.db.repository.shared.OrderedEntity<E>>): List<E> {
        return orderingEntities.sortedBy { it.ordering }.map { it.value }.toList()
    }
}
