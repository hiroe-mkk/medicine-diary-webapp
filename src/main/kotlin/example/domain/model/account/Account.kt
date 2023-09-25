package example.domain.model.account

/**
 * アカウント
 */
class Account private constructor(val id: AccountId,
                                  val credential: Credential,
                                  val username: Username) {
    companion object {
        fun create(accountId: AccountId, credential: Credential): Account {
            return Account(accountId, credential, Username(""))
        }

        fun reconstruct(id: AccountId,
                        credential: Credential,
                        username: Username): Account {
            return Account(id, credential, username)
        }
    }
}