package example.application.query.medicationrecord

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*

@Component
abstract class JSONMedicationRecordQueryService(private val medicineQueryService: MedicineQueryService,
                                                private val sharedGroupQueryService: SharedGroupQueryService) {
    // TODO: ドメインサービスを呼び出さないように変更する

    /**
     * 服用記録一覧ページを取得する
     */
    fun findJSONMedicationRecordsPage(userSession: UserSession,
                                      filter: MedicationRecordFilter,
                                      pageable: Pageable): JSONMedicationRecords {
        val filteredAccountIds = filteredAccountIds(filter, userSession)
        val filteredMedicineIds = filteredMedicineIds(filter, userSession)

        val page = if (filteredMedicineIds.isNotEmpty()) {
            findFilteredMedicationRecordsPage(filteredAccountIds,
                                              filteredMedicineIds,
                                              filter.start,
                                              filter.end,
                                              pageable,
                                              userSession.accountId)
        } else {
            Page.empty()
        }

        return JSONMedicationRecords.from(page)
    }

    private fun filteredMedicineIds(filter: MedicationRecordFilter,
                                    userSession: UserSession): Collection<MedicineId> {
        val medicineIds = medicineQueryService.findAllViewableMedicines(userSession.accountId).map { it.id }
        if (filter.medicineid == null) return medicineIds

        return if (medicineIds.contains(filter.medicineid)) listOf(filter.medicineid) else emptyList()
    }

    private fun filteredAccountIds(filter: MedicationRecordFilter,
                                   userSession: UserSession): Collection<AccountId> {
        val accountIds = sharedGroupQueryService.findParticipatingSharedGroup(userSession.accountId)?.members
                         ?: listOf(userSession.accountId)
        if (filter.accountid == null) return accountIds

        return if (accountIds.contains(filter.accountid)) listOf(filter.accountid) else emptyList()
    }

    abstract fun findFilteredMedicationRecordsPage(accountIds: Collection<AccountId>,
                                                   medicineIds: Collection<MedicineId>,
                                                   startedDate: LocalDate?,
                                                   endDate: LocalDate?,
                                                   pageable: Pageable,
                                                   requester: AccountId): Page<JSONMedicationRecord>
}