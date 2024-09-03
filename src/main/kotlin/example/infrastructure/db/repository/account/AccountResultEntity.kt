package example.infrastructure.db.repository.account

import example.domain.model.account.*

class AccountResultEntity(val accountId: AccountId,
                          val credentialType: String) {
    fun toAccount(credential: Credential): Account = Account.reconstruct(accountId, credential)
}
