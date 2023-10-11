package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisTakingRecordOverviewQueryService(private val takingRecordOverviewMapper: TakingRecordOverviewMapper)
    : TakingRecordOverviewQueryService {
    @Transactional(readOnly = true)
    override fun findTakingRecordDetailsByMedicineId(medicineId: MedicineId,
                                                     userSession: UserSession,
                                                     pageable: Pageable): Page<TakingRecordOverview> {
        val total = takingRecordOverviewMapper.countByMedicineIdAndAccountId(medicineId.value,
                                                                             userSession.accountId.value)
        val content = takingRecordOverviewMapper.findAllByMedicineIdAndAccountId(medicineId.value,
                                                                                 userSession.accountId.value,
                                                                                 pageable.pageSize,
                                                                                 pageable.offset)
        return PageImpl(content, pageable, total)
    }
}