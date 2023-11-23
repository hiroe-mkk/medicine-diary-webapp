package example.infrastructure.query.medicationrecord

import example.application.query.medicationrecord.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import java.time.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicationRecordQueryService(private val jsonMedicationRecordMapper: JSONMedicationRecordMapper,
                                              medicineQueryService: MedicineQueryService,
                                              sharedGroupQueryService: SharedGroupQueryService)
    : JSONMedicationRecordQueryService(medicineQueryService, sharedGroupQueryService) {
    override fun findFilteredMedicationRecordsPage(accountIds: Collection<AccountId>,
                                                   medicineIds: Collection<MedicineId>,
                                                   startedDate: LocalDate?,
                                                   endDate: LocalDate?,
                                                   pageable: Pageable,
                                                   requester: AccountId): Page<JSONMedicationRecord> {
        val accountIdValues = accountIds.map { it.value }
        val medicineIdValues = medicineIds.map { it.value }

        val total = jsonMedicationRecordMapper.countByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                            medicineIdValues,
                                                                                            startedDate,
                                                                                            endDate)
        val content = jsonMedicationRecordMapper.findAllByAccountIdsAndMedicineIdsAndRecorderAt(accountIdValues,
                                                                                                medicineIdValues,
                                                                                                startedDate,
                                                                                                endDate,
                                                                                                pageable.pageSize,
                                                                                                pageable.offset,
                                                                                                requester.value)
        return PageImpl(content, pageable, total)
    }
}