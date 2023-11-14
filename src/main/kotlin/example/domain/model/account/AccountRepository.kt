package example.domain.model.account

interface AccountRepository {
    fun createAccountId(): AccountId

    fun findById(accountId: AccountId): Account?

    fun findByCredential(credential: Credential): Account?

    fun save(account: Account)

    fun deleteById(accountId: AccountId)
}