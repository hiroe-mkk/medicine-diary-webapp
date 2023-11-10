package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import java.time.*

@Component
@Transactional(readOnly = true)
class MyBatisTakingRecordQueryService(private val takingRecordOverviewMapper: TakingRecordOverviewMapper,
                                      private val takingRecordDetailMapper: TakingRecordDetailMapper,
                                      medicineDomainService: MedicineDomainService)
    : TakingRecordQueryService(medicineDomainService) {
    override fun findTakingRecordDetailByTakingRecordId(takingRecordId: TakingRecordId): TakingRecordDetail? {
        return takingRecordDetailMapper.findOneByTakingRecordIdAndRecorder(takingRecordId.value)
    }

    override fun findFilteredTakingRecordOverviewsPage(accountIds: Collection<AccountId>,
                                                       medicineIds: Collection<MedicineId>,
                                                       startedDate: LocalDate?,
                                                       endDate: LocalDate?,
                                                       pageable: Pageable): Page<TakingRecordOverview> {
        val accountIdValues = accountIds.map { it.value }
        val medicineIdValues = medicineIds.map { it.value }

        val total = takingRecordOverviewMapper.countByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                            medicineIdValues,
                                                                                            startedDate,
                                                                                            endDate)
        val content = takingRecordOverviewMapper.findAllByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                                medicineIdValues,
                                                                                                startedDate,
                                                                                                endDate,
                                                                                                pageable.pageSize,
                                                                                                pageable.offset)
        return PageImpl(content, pageable, total)
    }
}