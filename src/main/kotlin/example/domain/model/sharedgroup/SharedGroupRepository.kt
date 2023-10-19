package example.domain.model.sharedgroup

interface SharedGroupRepository {
    fun findById(sharedGroupId: SharedGroupId): SharedGroup?

    fun save(sharedGroup: SharedGroup)

    fun delete(sharedGroupId: SharedGroupId)
}