package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface DisplayTakingRecordMapper {
    fun countByAccountIdsAndMedicineIdsAndRecorderAt(accountIds: List<String>,
                                                     medicineIds: List<String>,
                                                     start: LocalDate?,
                                                     end: LocalDate?): Long

    fun findAllByAccountIdsAndMedicineIdsAndRecorderAt(accountIds: List<String>,
                                                       medicineIds: List<String>,
                                                       start: LocalDate?,
                                                       end: LocalDate?,
                                                       pageSize: Int,
                                                       offset: Long): List<DisplayTakingRecord>
}