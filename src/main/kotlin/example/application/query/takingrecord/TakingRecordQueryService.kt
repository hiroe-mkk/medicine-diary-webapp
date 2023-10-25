package example.application.query.takingrecord

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*

interface TakingRecordQueryService {
    /**
     * 服用した薬をもとに服用記録概要一覧を取得する
     */
    fun findTakingRecordDetailsByTakenMedicine(medicineId: MedicineId,
                                               userSession: UserSession,
                                               pageable: Pageable): Page<TakingRecordOverview>

    /**
     * 服用記録を取得する
     */
    fun findTakingRecordDetail(takingRecordId: TakingRecordId,
                               userSession: UserSession): TakingRecordDetail
}