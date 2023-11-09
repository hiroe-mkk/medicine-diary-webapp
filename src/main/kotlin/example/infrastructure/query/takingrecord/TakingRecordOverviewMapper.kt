package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import org.apache.ibatis.annotations.*

@Mapper
interface TakingRecordOverviewMapper {
    fun countByTakenMedicineAndRecorder(medicineId: String): Long

    fun findAllByTakenMedicineAndRecorder(medicineId: String,
                                          pageSize: Int,
                                          offset: Long): MutableList<TakingRecordOverview>
}