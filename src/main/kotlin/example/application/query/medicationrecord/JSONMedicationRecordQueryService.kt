package example.application.query.medicationrecord

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*

@Component
abstract class JSONMedicationRecordQueryService(private val medicineQueryService: MedicineQueryService) {
    // TODO: クエリサービスにおいてドメインサービスを呼び出すべきではない？
    /**
     * 服用記録一覧ページを取得する
     */
    fun findJSONMedicationRecordsPage(userSession: UserSession,
                                      filter: MedicationRecordFilter,
                                      pageable: Pageable): Page<JSONMedicationRecord> {
        if (filter.accountids.isEmpty()) return Page.empty()

        val viewableMedicineIds = requireViewableMedicineIds(filter, userSession)
        if (viewableMedicineIds.isEmpty()) return Page.empty()

        return findFilteredMedicationRecordsPage(filter.accountids,
                                                 viewableMedicineIds,
                                                 filter.start,
                                                 filter.end,
                                                 pageable,
                                                 userSession.accountId)
    }

    private fun requireViewableMedicineIds(filter: MedicationRecordFilter,
                                           userSession: UserSession): Collection<MedicineId> {
        return if (filter.medicineid == null) {
            medicineQueryService.findAllViewableMedicines(userSession.accountId).map { it.id }
        } else {
            val viewableMedicine = medicineQueryService.findViewableMedicine(filter.medicineid,
                                                                             userSession.accountId)
            if (viewableMedicine != null) listOf(viewableMedicine.id) else listOf()
        }
    }

    abstract fun findFilteredMedicationRecordsPage(accountIds: Collection<AccountId>,
                                                   medicineIds: Collection<MedicineId>,
                                                   startedDate: LocalDate?,
                                                   endDate: LocalDate?,
                                                   pageable: Pageable,
                                                   requester: AccountId): Page<JSONMedicationRecord>
}