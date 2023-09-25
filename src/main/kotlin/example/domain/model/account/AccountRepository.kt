package example.domain.model.account

interface AccountRepository {
    fun nextIdentity(): AccountId

    fun findById(accountId: AccountId): Account?

    fun findByCredential(credential: Credential): Account?

    fun save(account: Account)
}