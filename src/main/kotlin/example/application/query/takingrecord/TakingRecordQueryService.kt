package example.application.query.takingrecord

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*

@Component
abstract class TakingRecordQueryService(private val medicineDomainService: MedicineDomainService) {
    // TODO: クエリサービスにおいてドメインサービスを呼び出すべきではない？
    /**
     * 服用記録一覧ページを取得する
     */
    fun findTakingRecordsPage(userSession: UserSession,
                              filter: TakingRecordFilter,
                              pageable: Pageable): Page<DisplayTakingRecord> {
        if (filter.members.isEmpty()) return Page.empty()

        val viewableMedicineIds = requireViewableMedicineIds(filter, userSession)
        if (viewableMedicineIds.isEmpty()) return Page.empty()

        return findFilteredTakingRecordsPage(filter.members,
                                             viewableMedicineIds,
                                             filter.start,
                                             filter.end,
                                             pageable)
    }

    private fun requireViewableMedicineIds(filter: TakingRecordFilter,
                                           userSession: UserSession): Collection<MedicineId> {
        return if (filter.medicine == null) {
            medicineDomainService.findAllViewableMedicines(userSession.accountId).map { it.id }
        } else {
            val viewableMedicine = medicineDomainService.findViewableMedicine(filter.medicine,
                                                                              userSession.accountId)
            if (viewableMedicine != null) listOf(viewableMedicine.id) else listOf()
        }
    }

    abstract fun findFilteredTakingRecordsPage(accountIds: Collection<AccountId>,
                                               medicineIds: Collection<MedicineId>,
                                               startedDate: LocalDate?,
                                               endDate: LocalDate?,
                                               pageable: Pageable): Page<DisplayTakingRecord>
}