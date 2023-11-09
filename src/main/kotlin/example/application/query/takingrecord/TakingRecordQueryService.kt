package example.application.query.takingrecord

import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.Component

@Component
abstract class TakingRecordQueryService(private val medicineDomainService: MedicineDomainService) {
    // TODO: クエリサービスにおいてドメインサービスを呼び出すべきではない？

    /**
     * 服用した薬をもとに服用記録概要一覧を取得する
     */
    fun findTakingRecordOverviewsByTakenMedicine(medicineId: MedicineId,
                                                 userSession: UserSession,
                                                 pageable: Pageable): Page<TakingRecordOverview> {
        return medicineDomainService.findViewableMedicine(medicineId, userSession.accountId)
                   ?.let { findTakingRecordOverviewsByViewableMedicine(it, pageable) }
               ?: Page.empty()
    }

    abstract fun findTakingRecordOverviewsByViewableMedicine(medicine: Medicine,
                                                             pageable: Pageable): Page<TakingRecordOverview>

    /**
     * 服用記録を取得する
     */
    fun findTakingRecordDetail(takingRecordId: TakingRecordId,
                               userSession: UserSession): TakingRecordDetail {
        return findTakingRecordDetailByTakingRecordId(takingRecordId)
                   ?.let {
                       if (medicineDomainService.isViewableMedicine(it.medicineId, userSession.accountId)) it else null
                   } ?: throw TakingRecordNotFoundException(takingRecordId)
    }

    abstract fun findTakingRecordDetailByTakingRecordId(takingRecordId: TakingRecordId): TakingRecordDetail?
}