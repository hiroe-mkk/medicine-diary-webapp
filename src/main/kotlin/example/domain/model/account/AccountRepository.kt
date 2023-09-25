package example.domain.model.account

interface AccountRepository {
    fun findById(accountId: AccountId): Account?

    fun save(account: Account)
}