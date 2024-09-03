package example.infrastructure.db.query.medicationrecord

import example.application.query.medicationrecord.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface JSONMedicationRecordMapper {
    fun countByAccountIdsAndMedicineIdsAndRecorderAt(accountIds: Collection<String>,
                                                     medicineIds: Collection<String>,
                                                     start: LocalDate?,
                                                     end: LocalDate?): Long

    fun findAllByAccountIdsAndMedicineIdsAndRecorderAt(accountIds: Collection<String>,
                                                       medicineIds: Collection<String>,
                                                       start: LocalDate?,
                                                       end: LocalDate?,
                                                       pageSize: Int,
                                                       offset: Long,
                                                       requester: String): List<JSONMedicationRecord>
}
