package example.infrastructure.query.medicine

import example.application.query.medicine.*
import org.apache.ibatis.annotations.*

@Mapper
interface JsonMedicineOverviewsMapper {
    fun findAllAvailableMedicineOverviews(accountId: String): List<JSONMedicineOverview>
}