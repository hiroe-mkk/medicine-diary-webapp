package example.application.query.takingrecord

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import org.springframework.data.domain.*

interface TakingRecordOverviewQueryService {
    /**
     * 薬IDをもとに服用記録概要一覧を取得する
     */
    fun findTakingRecordDetailsByMedicineId(medicineId: MedicineId,
                                            userSession: UserSession,
                                            pageable: Pageable): Page<TakingRecordOverview>
}