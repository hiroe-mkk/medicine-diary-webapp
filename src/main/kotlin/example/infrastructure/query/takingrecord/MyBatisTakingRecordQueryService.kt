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
@Transactional(readOnly = true)
class MyBatisTakingRecordQueryService(private val takingRecordOverviewMapper: TakingRecordOverviewMapper,
                                      private val takingRecordDetailMapper: TakingRecordDetailMapper)
    : TakingRecordQueryService {

    override fun findTakingRecordDetailsByTakenMedicine(medicineId: MedicineId,
                                                        userSession: UserSession,
                                                        pageable: Pageable): Page<TakingRecordOverview> {
        val total = takingRecordOverviewMapper.countByTakenMedicineAndRecorder(medicineId.value,
                                                                               userSession.accountId.value)
        val content = takingRecordOverviewMapper.findAllByTakenMedicineAndRecorder(medicineId.value,
                                                                                   userSession.accountId.value,
                                                                                   pageable.pageSize,
                                                                                   pageable.offset)
        return PageImpl(content, pageable, total)
    }

    override fun findTakingRecordDetail(takingRecordId: TakingRecordId, userSession: UserSession): TakingRecordDetail {
        return takingRecordDetailMapper.findOneByTakingRecordIdAndRecorder(takingRecordId.value,
                                                                           userSession.accountId.value)
               ?: throw TakingRecordNotFoundException(takingRecordId)
    }
}