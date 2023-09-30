package example.domain.model.account

import example.domain.model.account.profile.*

/**
 * アカウント
 */
class Account private constructor(val id: AccountId,
                                  val credential: Credential) {
    companion object {
        fun create(id: AccountId,
                   credential: Credential): Pair<Account, Profile> {
            val account = Account(id, credential)
            val profile = Profile(id, Username.creteDefaultUsername(), null)
            return Pair(account, profile)
        }

        fun reconstruct(id: AccountId,
                        credential: Credential): Account {
            return Account(id, credential)
        }
    }
}