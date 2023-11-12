package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import java.time.*

@Component
@Transactional(readOnly = true)
class MyBatisTakingRecordQueryService(private val jsonTakingRecordMapper: JSONTakingRecordMapper,
                                      medicineDomainService: MedicineDomainService)
    : TakingRecordQueryService(medicineDomainService) {
    override fun findFilteredTakingRecordsPage(accountIds: Collection<AccountId>,
                                               medicineIds: Collection<MedicineId>,
                                               startedDate: LocalDate?,
                                               endDate: LocalDate?,
                                               pageable: Pageable,
                                               requester: AccountId): Page<JSONTakingRecord> {
        val accountIdValues = accountIds.map { it.value }
        val medicineIdValues = medicineIds.map { it.value }

        val total = jsonTakingRecordMapper.countByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                        medicineIdValues,
                                                                                        startedDate,
                                                                                        endDate)
        val content = jsonTakingRecordMapper.findAllByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                            medicineIdValues,
                                                                                            startedDate,
                                                                                            endDate,
                                                                                            pageable.pageSize,
                                                                                            pageable.offset,
                                                                                            requester.value)
        return PageImpl(content, pageable, total)
    }
}