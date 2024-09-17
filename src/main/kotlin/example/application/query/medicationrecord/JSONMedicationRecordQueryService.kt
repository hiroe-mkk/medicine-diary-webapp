package example.application.query.medicationrecord

import example.application.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*

@Component
interface JSONMedicationRecordQueryService {
    /**
     * 服用記録一覧ページを取得する
     */
    fun getMedicationRecordsPage(filter: MedicationRecordFilter,
                                 pageable: Pageable,
                                 userSession: UserSession): JSONMedicationRecords
}
