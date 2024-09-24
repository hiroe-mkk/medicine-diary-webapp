package example.infrastructure.db.query.medicationrecord

import example.application.query.medicationrecord.*
import example.application.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicationRecordQueryService(private val jsonMedicationRecordMapper: JSONMedicationRecordMapper)
    : JSONMedicationRecordQueryService {

    override fun getMedicationRecordsPage(filter: MedicationRecordFilter,
                                          pageable: Pageable,
                                          userSession: UserSession): JSONMedicationRecords {
        val total = if (filter.isEmpty()) {
            jsonMedicationRecordMapper.countAll(userSession.accountId.value)
        } else {
            jsonMedicationRecordMapper.countAllByAccountIdAndMedicineIdAndRecorderAt(filter.nonBlankMedicineId,
                                                                                     filter.nonBlankAccountId,
                                                                                     filter.start,
                                                                                     filter.end,
                                                                                     userSession.accountId.value)
        }

        val content = if (total != 0L) {
            if (filter.isEmpty()) {
                jsonMedicationRecordMapper.findAll(if (pageable.pageSize <= 100) pageable.pageSize else 100,
                                                   pageable.offset,
                                                   userSession.accountId.value)
            } else {
                jsonMedicationRecordMapper.findAllByAccountIdAndMedicineIdAndRecorderAt(filter.nonBlankMedicineId,
                                                                                        filter.nonBlankAccountId,
                                                                                        filter.start,
                                                                                        filter.end,
                                                                                        if (pageable.pageSize <= 100) pageable.pageSize else 100,
                                                                                        pageable.offset,
                                                                                        userSession.accountId.value)
            }
        } else {
            emptyList()
        }

        return JSONMedicationRecords.from(PageImpl(content, pageable, total))
    }
}
