package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisTakingRecordQueryService(private val takingRecordOverviewMapper: TakingRecordOverviewMapper,
                                      private val takingRecordDetailMapper: TakingRecordDetailMapper,
                                      medicineDomainService: MedicineDomainService)
    : TakingRecordQueryService(medicineDomainService) {

    override fun findTakingRecordOverviewsByViewableMedicine(medicine: Medicine,
                                                             pageable: Pageable): Page<TakingRecordOverview> {
        val total = takingRecordOverviewMapper.countByTakenMedicineAndRecorder(medicine.id.value)
        val content = takingRecordOverviewMapper.findAllByTakenMedicineAndRecorder(medicine.id.value,
                                                                                   pageable.pageSize,
                                                                                   pageable.offset)
        return PageImpl(content, pageable, total)
    }

    override fun findTakingRecordDetailByTakingRecordId(takingRecordId: TakingRecordId): TakingRecordDetail? {
        return takingRecordDetailMapper.findOneByTakingRecordIdAndRecorder(takingRecordId.value)
    }
}