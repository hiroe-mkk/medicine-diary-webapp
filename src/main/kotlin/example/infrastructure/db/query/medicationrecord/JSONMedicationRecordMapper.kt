package example.infrastructure.db.query.medicationrecord

import example.application.query.medicationrecord.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface JSONMedicationRecordMapper {
    fun countAll(requester: String): Long

    fun countAllByAccountIdAndMedicineIdAndRecorderAt(medicineId: String?,
                                                      accountId: String?,
                                                      start: LocalDate?,
                                                      end: LocalDate?,
                                                      requester: String): Long

    fun findAll(pageSize: Int, offset: Long, requester: String): List<JSONMedicationRecord>

    fun findAllByAccountIdAndMedicineIdAndRecorderAt(medicineId: String?,
                                                     accountId: String?,
                                                     start: LocalDate?,
                                                     end: LocalDate?,
                                                     pageSize: Int,
                                                     offset: Long,
                                                     requester: String): List<JSONMedicationRecord>
}
