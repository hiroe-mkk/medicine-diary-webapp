package example.application.query.takingrecord

import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.time.*

@Component
abstract class TakingRecordQueryService(private val medicineDomainService: MedicineDomainService) {
    // TODO: クエリサービスにおいてドメインサービスを呼び出すべきではない？

    /**
     * 服用記録詳細を取得する
     */
    fun findTakingRecordDetail(takingRecordId: TakingRecordId,
                               userSession: UserSession): TakingRecordDetail {
        return findTakingRecordDetailByTakingRecordId(takingRecordId)
                   ?.let {
                       if (medicineDomainService.isViewableMedicine(it.medicineId, userSession.accountId)) it else null
                   } ?: throw TakingRecordNotFoundException(takingRecordId)
    }

    /**
     * 服用記録概要一覧ページをを取得する
     */
    fun findTakingRecordOverviewsPage(userSession: UserSession,
                                      filter: TakingRecordOverviewsFilter,
                                      pageable: Pageable): Page<TakingRecordOverview> {
        if (filter.members.isEmpty()) return Page.empty()

        val viewableMedicineIds = requireViewableMedicineIds(filter, userSession)
        if (viewableMedicineIds.isEmpty()) return Page.empty()

        return findFilteredTakingRecordOverviewsPage(filter.members,
                                                     viewableMedicineIds,
                                                     filter.start,
                                                     filter.end,
                                                     pageable)
    }

    private fun requireViewableMedicineIds(filter: TakingRecordOverviewsFilter,
                                           userSession: UserSession): Collection<MedicineId> {
        return if (filter.medicine == null) {
            medicineDomainService.findAllViewableMedicines(userSession.accountId).map { it.id }
        } else {
            val viewableMedicine = medicineDomainService.findViewableMedicine(filter.medicine,
                                                                              userSession.accountId)
            if (viewableMedicine != null) listOf(viewableMedicine.id) else listOf()
        }
    }

    abstract fun findTakingRecordDetailByTakingRecordId(takingRecordId: TakingRecordId): TakingRecordDetail?

    abstract fun findFilteredTakingRecordOverviewsPage(accountIds: Collection<AccountId>,
                                                       medicineIds: Collection<MedicineId>,
                                                       startedDate: LocalDate?,
                                                       endDate: LocalDate?,
                                                       pageable: Pageable): Page<TakingRecordOverview>
}