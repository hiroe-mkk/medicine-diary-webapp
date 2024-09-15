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
class AccountDeletionService(private val accountRepository: AccountRepository,
                             private val profileRepository: ProfileRepository,
                             private val medicationRecordRepository: MedicationRecordRepository,
                             private val sharedGroupLeaveService: SharedGroupLeaveService,
                             private val medicineDeletionCoordinator: MedicineDeletionCoordinator) {
    /**
     * アカウントを削除する
     */
    fun deleteAccount(userSession: UserSession) {
        val account = accountRepository.findById(userSession.accountId) ?: return

        medicationRecordRepository.deleteByRecorder(account.id)
        medicineDeletionCoordinator.deleteAllOwnedMedicinesAndMedicationRecords(userSession.accountId)
        sharedGroupLeaveService.leaveSharedGroup(userSession.accountId)

        profileRepository.deleteByAccountId(account.id)
        accountRepository.deleteById(account.id)
    }
}
