package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisTakingRecordQueryService(private val takingRecordOverviewMapper: TakingRecordOverviewMapper,
                                      private val takingRecordDetailMapper: TakingRecordDetailMapper)
    : TakingRecordQueryService {

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

    /**
     * 服用記録を取得する
     */
    @Transactional(readOnly = true)
    override fun findTakingRecordDetail(takingRecordId: TakingRecordId, userSession: UserSession): TakingRecordDetail {
        return takingRecordDetailMapper.findOneByTakingRecordIdAndRecorder(takingRecordId.value,
                                                                           userSession.accountId.value)
               ?: throw TakingRecordNotFoundException(takingRecordId)
    }
}