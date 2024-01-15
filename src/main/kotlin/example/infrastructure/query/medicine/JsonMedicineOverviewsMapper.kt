package example.infrastructure.query.medicine

import example.application.query.medicine.*
import org.apache.ibatis.annotations.*

@Mapper
interface JsonMedicineOverviewsMapper {
    fun findAllAvailableMedicineOverviews(accountId: String): List<JSONMedicineOverview>

    fun findAllOwnedMedicineOverviews(accountId: String): List<JSONMedicineOverview>

    fun findAllSharedGroupMedicineOverviews(accountId: String): List<JSONMedicineOverview>

    fun findAllMembersMedicineOverviews(accountId: String): List<JSONMedicineOverview>
}