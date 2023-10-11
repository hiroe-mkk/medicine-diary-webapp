package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import org.apache.ibatis.annotations.*

@Mapper
interface TakingRecordOverviewMapper {
    fun countByMedicineIdAndAccountId(medicineId: String, accountId: String): Long

    fun findAllByMedicineIdAndAccountId(medicineId: String,
                                        accountId: String,
                                        pageSize: Int,
                                        offset: Long): MutableList<TakingRecordOverview>
}