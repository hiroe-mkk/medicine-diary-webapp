package example.infrastructure.db.query.medicationrecord

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
                                              sharedGroupRepository: SharedGroupRepository,
                                              medicineQueryService: MedicineQueryService)
    : JSONMedicationRecordQueryService(medicineQueryService, sharedGroupRepository) {
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
                                                                                                if (pageable.pageSize <= 100) pageable.pageSize else 100,
                                                                                                pageable.offset,
                                                                                                requester.value)
        return PageImpl(content, pageable, total)
    }
}
