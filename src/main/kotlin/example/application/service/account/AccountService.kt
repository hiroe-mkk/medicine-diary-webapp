package example.application.service.account

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class AccountService(private val accountRepository: AccountRepository,
                     private val profileRepository: ProfileRepository,
                     private val sharedGroupRepository: SharedGroupRepository,
                     private val medicationRecordRepository: MedicationRecordRepository,
                     private val sharedGroupQueryService: SharedGroupQueryService,
                     private val sharedGroupUnshareService: SharedGroupUnshareService,
                     private val medicineDeletionService: MedicineDeletionService) {
    /**
     * アカウントを取得または作成する
     *
     * 指定されたクレデンシャルを持つアカウントを返すか、見つからなかった場合は作成した結果を返す
     */
    fun findOrElseCreateAccount(credential: Credential): Account {
        return accountRepository.findByCredential(credential) ?: createAccount(credential)
    }

    private fun createAccount(credential: Credential): Account {
        val accountId = accountRepository.createAccountId()
        val (account, profile) = Account.create(accountId, credential)
        accountRepository.save(account)
        profileRepository.save(profile)
        return account
    }

    /**
     * アカウントを削除する
     */
    fun deleteAccount(userSession: UserSession) {
        val account = accountRepository.findById(userSession.accountId) ?: return
        medicationRecordRepository.deleteByRecorder(account.id)
        medicineDeletionService.deleteAllOwnedMedicines(userSession.accountId)
        sharedGroupUnshareService.unshare(userSession.accountId)
        val invitedSharedGroups = sharedGroupQueryService.findInvitedSharedGroups(userSession.accountId)
        invitedSharedGroups.forEach {
            it.rejectInvitation(userSession.accountId)
            sharedGroupRepository.save(it)
        }

        profileRepository.deleteByAccountId(account.id)
        accountRepository.deleteById(account.id)
    }
}